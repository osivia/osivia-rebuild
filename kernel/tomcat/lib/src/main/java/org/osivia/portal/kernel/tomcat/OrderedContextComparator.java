package org.osivia.portal.kernel.tomcat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.catalina.Container;
import org.apache.commons.lang3.StringUtils;

public class OrderedContextComparator implements Comparator<Object> {

	private List<String> orderedContexts = null;
	private final String prefix;
	private final String suffix;

	public OrderedContextComparator(String prefix, String suffix) {
		super();
		this.prefix = prefix;
		this.suffix = suffix;
	}

	private List<String> getOrdererContexts() {
		if (orderedContexts == null) {
			orderedContexts = new ArrayList<>();
			String order = System.getProperty("portal.deploy.order");
			if (StringUtils.isNotEmpty(order)) {
				String tokens[] = order.split("\\|");
				for (int i = 0; i < tokens.length; i++) {
					orderedContexts.add(prefix + tokens[i] + suffix);
				}
			}
		}
		return orderedContexts;
	}

	@Override
	public int compare(Object o1, Object o2) {
		
		String s1;
		String s2;
		
		if( o1 instanceof Container)
			s1 = ((Container) o1).getName();
		else
			s1 = o1.toString();
		
		if( o2 instanceof Container)
			s2 = ((Container) o2).getName();
		else
			s2 = o2.toString();		

		int i1 = getOrdererContexts().indexOf(s1);
		int i2 = getOrdererContexts().indexOf(s2);

		if (i1 != -1) {
			if (i2 != -1) {
				return (i1 - i2);
			} else {
				return -1;
			}

		} else {
			if (i2 != -1) {
				return 1;
			} else {
				return s1.compareTo(s2);
			}
		}

	}

}
