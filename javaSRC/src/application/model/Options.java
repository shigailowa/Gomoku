package application.model;

import java.util.HashMap;
import java.util.HashSet;

import org.omg.CORBA.PUBLIC_MEMBER;

import jdk.internal.dynalink.beans.StaticClass;

public class Options {
	
	
	private HashSet<Tupel> _menge;
	
	
	public Options(HashSet<Tupel>) {
		
	}
	
	public static class Tupel {
		
		public String _name;
		
		public Object _objekt;
		
		public Tupel(String name, Object objekt) {
	
			_name = name;
			_objekt = objekt;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tupel other = (Tupel) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (_name == null) {
				if (other._name != null)
					return false;
			} else if (!_name.equals(other._name))
				return false;
			return true;
		}

		private Options getOuterType() {
			return Options.this;
		}
			
	}
	

}
