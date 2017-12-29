package application;
	
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import jdk.internal.dynalink.beans.StaticClass;
import javafx.scene.Scene;

public class Main extends Application {
	
	public static boolean DEBUG=false;
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			FXMLLoader loader=new FXMLLoader();
		    loader.setLocation(Main.class.getResource("view/GameLayout.fxml"));
		    Scene spielScene = new Scene(loader.load());

		    spielScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(spielScene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
	/* testing things, ignore this
	 	class foo
		{
			foo parent;
			foo child;
			int value;
			
			public foo(int value) 
			{
				this.value=value;
			}
			
			public foo(foo parent, int value)
			{
				this.parent=parent;
				parent.child=this;
				this.value=value;
			}

			public String toString()
			{
				String p="p:"+((parent!=null)?parent.value:"n")+" ";
				String c=" c:"+((child!=null)?child.toString():"n")+" ";
				return "("+p+"v:"+value+c+" h:"+hashCode()+")";
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((child == null) ? 0 : directedHash(1,1));
				result = prime * result + ((parent == null) ? 0 : directedHash(-1,1));
				result = prime * result + value;
				return result;
			}
			
			private int directedHash(int dir, int r)
			{
				final int prime = 37;

				System.out.println(r);
				if(dir>0)
					return child==null?prime*r+value:prime*r+child.directedHash(dir,prime*r+value);
				return parent==null?prime*r+value:prime*r+parent.directedHash(dir,prime*r+value);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				foo other = (foo) obj;
				if (child == null) {
					if (other.child != null)
						return false;
				} else if (!child.equals(other.child))
					return false;
				if (parent == null) {
					if (other.parent != null)
						return false;
				} else if (!parent.equals(other.parent))
					return false;
				if (value != other.value)
					return false;
				return true;
			}
		}

		Set<foo> s=new LinkedHashSet<foo>();
		System.out.println(s);
		
		s.add(new foo(1));
		System.out.println(s);
		
		foo n=new foo(1);
		s.add(n);
		System.out.println(s);

		s.add(new foo(n,2));
		System.out.println(s);
		
		s.add(new foo(100));
		System.out.println(s);

		s.add(new foo(101));
		System.out.println(s);
		
		s.remove(new foo(100));
		System.out.println(s);
s*/
		
		launch(args);
	}
}










