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
	
	// set for a given string an obj, if already present, overwrite
	public void setOption(String name, Object objekt)
	{
		Tupel tupelNeu = new Tupel(name, objekt);
		
		_menge.remove(tupelNeu);
		_menge.add(tupelNeu);
		
	}
	
	// if the string name is present, return the corresponding obj
	// otherwise null
	public Object getOption(String name)
	{	
		for (Tupel tupel : _menge)
			if (tupel.name == name)
				return tupel.objekt;
		
		return null;
	}
	
	public void printOption(String name)
	{
		System.out.print(name+": ");
		System.out.println(getOption(name));
	}
	
	@Override
	public String toString() {
		int longestName=0;
		for (Tupel tupel : _menge)
			if(tupel.name.length()>longestName)
				longestName=tupel.name.length();
		
		String erg = new String("Options: \n");
		for (Tupel tupel : _menge)
			erg+=String.format("%"+longestName+"s:", tupel.name)+tupel.objekt+"\n";

		return erg;
	}

	// inner class that wraps a string and an object
	private class Tupel
	{	
		public String name;	
		public Object objekt;
		
		public Tupel(String name, Object objekt)
		{
			this.name = name;
			this.objekt = objekt;
		}

		// generated methods so the tupel can be put inside a hashSet
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private Options getOuterType() {
			return Options.this;
		}

		@Override
		public String toString() {
			return "Tupel [name=" + name + ", objekt=" + objekt + "]";
		}
		
	}
}
