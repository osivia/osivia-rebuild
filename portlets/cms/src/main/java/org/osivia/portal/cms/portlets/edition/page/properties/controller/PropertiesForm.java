package org.osivia.portal.cms.portlets.edition.page.properties.controller;

import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PropertiesForm {
    
    private String id;
    

    private String layoutId;
    private String themeId;
    private String category;

    private boolean selectorsPropagation;




	private Map<String, String> layouts;
    private Map<String, String> themes;    
    private Map<String, String> categories;    
    



	public String getLayoutId() {
        return layoutId;
    }

    
    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }
    
   
    
    public String getThemeId() {
        return themeId;
    }


    
    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}

    
    public Map<String, String> getThemes() {
        return themes;
    }


    
    public void setThemes(Map<String, String> themes) {
        this.themes = themes;
    }


    public Map<String, String> getLayouts() {
        return layouts;
    }

    
    public void setLayouts(Map<String, String> layouts) {
        this.layouts = layouts;
    }

    public Map<String, String> getCategories() {
		return categories;
	}


	public void setCategories(Map<String, String> categories) {
		this.categories = categories;
	}
    
    
    public String getId() {
        return id;
    }


    
    public void setId(String id) {
        this.id = id;
    }

	public boolean isSelectorsPropagation() {
		return selectorsPropagation;
	}


	public void setSelectorsPropagation(boolean selectorsPropagation) {
		this.selectorsPropagation = selectorsPropagation;
	}


}
