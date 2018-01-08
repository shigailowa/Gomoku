package application.model;

import java.util.HashSet;

public class Options {
	
	 // holds every option that has been set
	private HashSet<Tupel> _menge;
	
	// create new empty options obj
	public Options()
	{
		_menge = new HashSet<Tupel>();
	}
	
	// if the string name is present, return the corresponding obj
	// otherwise null
	public Object getOption(String name)
	{	
		for (Tupel tupel : _menge)
			if (tupel._name == name)
				return tupel._objekt;
		
		return null;
	}
	
	// set for a given string an obj, if already present, overwrite
	public void setOption(String name, Object objekt)
	{
		Tupel tupelNeu = new Tupel(name, objekt);
		
		_menge.remove(tupelNeu);
		_menge.add(tupelNeu);
		
	}
	
	// inner class that wraps a string and an object
	private class Tupel
	{	
		public String _name;	
		public Object _objekt;
		
		public Tupel(String name, Object objekt)
		{
			_name = name;
			_objekt = objekt;
		}

		// generated methods so the tupel can be put inside a hashSet
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
