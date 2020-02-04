package ex04;

import java.lang.reflect.Method;
import java.util.Scanner;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.NotFoundException;

public class Driver {
	public static void main(String hi[]) {
		String args[] = null;
		String methods[] = null;
		Scanner input = new Scanner(System.in);
		
		System.out.print("Please enter the classes to edit: ");
		args = input.nextLine().split(" ");
		while (args.length != 3) {
			System.out.println("[WRN] Invalid input size!!");
			System.out.print("Please enter the classes to edit: ");
			args = input.nextLine().split(" ");
		}
		
		System.out.print("Please enter the usage method, increment method, and getter method: ");
		methods = input.nextLine().split(" ");
		while (true)
		{
			if (methods.length != 3)
			{
				System.out.println("[WRN] Invalid input size!!");
				System.out.print("Please enter the usage method, increment method, and getter method: ");
				methods = input.nextLine().split(" ");
				continue;
			}
			
			String method = "";
			if (methods[0].equals(methods[1]))
			{
				method = methods[0];
			}
			else if (methods[0].equals(methods[2]))
			{
				method = methods[0];
			}
			else if (methods[2].equals(methods[1]))
			{
				method = methods[1];
			}
			else
			{
				break;
			}
			
			System.out.printf("[WRN] This method '%s' has been modified!!\n", method);
			System.out.print("Please enter the usage method, increment method, and getter method: ");
			methods = input.nextLine().split(" ");
		}
		input.close();
		
		int index = 0;
		String common = "Common";

		if (args[0].startsWith(common)) {
			if (args[1].startsWith(common) && args[2].startsWith(common)) {
				if (args[0].length() > args[1].length() && args[0].length() > args[2].length()) {
					index = 0;
				} else if (args[1].length() > args[0].length() && args[1].length() > args[2].length()) {
					index = 1;
				} else {
					index = 2;
				}
			} else if (args[1].startsWith(common)) {
				if (args[1].length() > args[0].length()) {
					index = 1;
				} else {
					index = 0;
				}
			} else if (args[2].startsWith(common)) {
				if (args[0].length() > args[2].length()) {
					index = 0;
				} else {
					index = 2;
				}
			} else {
				index = 0;
			}
		} else if (args[1].startsWith(common)) {

			if (args[2].startsWith(common)) {
				if (args[1].length() > args[2].length()) {
					index = 1;
				} else {
					index = 2;
				}
			} else {
				index = 2;
			}
		} else if (args[2].startsWith(common)) {
			index = 2;
		}

		String superClass = args[index];
		
		for (int i = 0; i < args.length; i++) {
			if (i != index) {
				try {
					ClassPool pool = ClassPool.getDefault();
	
					CtClass cc = pool.get(args[i]);
					setSuperclass(cc, superClass, pool);
					
					String BLK1 = String.format("{\n\n\t%s();\n\tSystem.out.println(\"[TR] %s result : \" + %s());\n}", methods[1], methods[2], methods[2]);
					
					addMethodCall(pool, cc, BLK1, methods[0]);
					executeMethodCall(pool, args[i], methods[0]);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void setSuperclass(CtClass curClass, String superClass, ClassPool pool) throws NotFoundException, CannotCompileException {
	      curClass.setSuperclass(pool.get(superClass));
	      System.out.println("[DBG] set superclass: " + curClass.getSuperclass().getName() + //
	            ", subclass: " + curClass.getName());
	}
	
	static void addMethodCall(ClassPool cp, CtClass cc, String BLK1, String method) throws Exception {
      CtMethod m1 = cc.getDeclaredMethod(method);
      System.out.println("[DBG] Block: " + BLK1);
      m1.insertBefore(BLK1);
   }

   static void executeMethodCall(ClassPool cp, String className, String methodName) throws Exception {
      Loader cl = new Loader(cp);
      Class<?> c = cl.loadClass(className);
      Object ob = c.newInstance();
      System.out.println("[DBG] Created a " + className + " object.");

      Class<?> obClass = ob.getClass();
      Method m = obClass.getDeclaredMethod(methodName, new Class[] {});
      System.out.printf("[DBG] Called %s\n", methodName);
      Object obVal = m.invoke(c.newInstance(), new Object[] {});
      System.out.printf("[DBG] %s result: %s\n", methodName, obVal);
   }
}
