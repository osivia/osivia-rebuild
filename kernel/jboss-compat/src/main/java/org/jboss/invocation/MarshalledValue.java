package org.jboss.invocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

// FIXME : OSIVIA/MIG MOCK

public class MarshalledValue implements Externalizable {
	private static final long serialVersionUID = -1527598981234110311L;
	private byte[] serializedForm;
	private int hashCode;

	public MarshalledValue() {
	}

	public MarshalledValue(Object obj) throws IOException {
		
	}

	public Object get() throws IOException, ClassNotFoundException {
	return null;
	}

	public byte[] toByteArray() {
		return this.serializedForm;
	}

	public int size() {
		int size = this.serializedForm != null ? this.serializedForm.length : 0;
		return size;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else {
			boolean equals = false;
			if (obj instanceof MarshalledValue) {
				MarshalledValue mv = (MarshalledValue) obj;
				if (this.serializedForm == mv.serializedForm) {
					equals = true;
				} else {
					equals = Arrays.equals(this.serializedForm, mv.serializedForm);
				}
			}

			return equals;
		}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int length = in.readInt();
		this.serializedForm = null;
		if (length > 0) {
			this.serializedForm = new byte[length];
			in.readFully(this.serializedForm);
		}

		this.hashCode = in.readInt();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		int length = this.serializedForm != null ? this.serializedForm.length : 0;
		out.writeInt(length);
		if (length > 0) {
			out.write(this.serializedForm);
		}

		out.writeInt(this.hashCode);
	}
}