// This is supporting software for CS321 Compilers and Language Design I
// Copyright (c) Jingke Li, Portland State University

// 
// AST node definitions.
//   
// For CS321 Fall'13 Assignment 4.
//

import java.util.*;

class Ast {
  static int tab=0;	// indentation for printing AST.

  // Top-Level Node -----------------------------------------------------

  public abstract static class Node {

    // utility routines

    public static void DUMP(String s) { System.out.print(s); }
    public static void DUMP(int i) { System.out.print(i); }
    public static void DUMP(int tab, String s) {
      for (int i=0; i<tab; i++)
	System.out.print(" "); 
      System.out.print(s); 
    }
    public static void DUMP(Boolean bang, int tab, String s) {
      if (bang) {
	System.out.print("!");
	tab = tab-1;
      }
      for (int i=0; i<tab; i++)
	System.out.print(" "); 
      System.out.print(s); 
    }

    public static void DUMP(NodeList l) { 
      if (l!=null) l.dump(); 
    }

    public static void DUMP(Type t) { 
      if (t==null) DUMP("(Void) "); else t.dump(); 
    }

    public static void DUMP(Stmt s) { 
      if (s!=null) 
	s.dump(); 
    }

    public static void DUMP(Exp e) { 
      if (e==null) DUMP("() "); else e.dump(); 
    }

    public abstract void dump();

  }

  // Node Lists ---------------------------------------------------------
 
  public static class NodeList extends Node {
    Vector<Node> list;

    NodeList() { list = new Vector<Node>(); }

    void add(Node n)        { list.addElement(n); }
    void addAll(NodeList l) { list.addAll(l.list); }
    Node elementAt(int i)   { return list.elementAt(i); }
    int size()              { return list.size(); }


    public void dump() {
      for (int i=0; i<size(); i++) 
        elementAt(i).dump();
    }
  }   

  public static class ClassDeclList extends NodeList {
    ClassDeclList() { super(); }
    ClassDecl elementAt(int i) { return (ClassDecl)super.elementAt(i); }
    void setReachability() {
      for (int i=0; i<size(); i++)
        elementAt(i).setReachability();
    }
  }   

  public static class MethodDeclList extends NodeList {
    MethodDeclList() { super(); }
    MethodDecl elementAt(int i) { return (MethodDecl)super.elementAt(i); }
    void setReachability() {
      for (int i=0; i<size(); i++)
        elementAt(i).setReachability();
    }

  }   

  public static class VarDeclList extends NodeList {
    VarDeclList() { super(); }
    VarDecl elementAt(int i)  { return (VarDecl)super.elementAt(i); }
  }

  public static class FormalList extends NodeList {
    FormalList() { super(); }
    Formal elementAt(int i)  { return (Formal)super.elementAt(i); }
    public void dump() { DUMP("("); super.dump(); DUMP(")"); }
  }

  public static class StmtList extends NodeList {
    StmtList() { super(); }
    Stmt elementAt(int i)  { return (Stmt)super.elementAt(i); }
    public void dump() {
      for (int i=0; i<size(); i++) 
        DUMP(elementAt(i));
    }

    // StmtList contains a list of Stmt
    // Set the reachability of each Stmt in StmtList
    // pass in the notReachable boolean each time.
    boolean setReachability(boolean notReachable) {
      boolean stmtListHasReturned = false;
      for (int i=0; i<size(); i++)
        stmtListHasReturned = elementAt(i).setReachability(stmtListHasReturned);
      return stmtListHasReturned;
    }
  }

  public static class ExpList extends NodeList {
    ExpList() { super(); }
    Exp elementAt(int i)  { return (Exp)super.elementAt(i); }
    public void dump() { DUMP("("); super.dump(); DUMP(")"); }
  }   

  // Program Node -------------------------------------------------------

  public static class Program extends Node {
    ClassDeclList cl;	// classes

    Program(ClassDeclList acl) { cl=acl; }

    public void dump() { 
      DUMP("Program\n"); DUMP(cl);
    }
    public void dump(int i) {}

    public void setReachability() {
      // Programs contain a ClassDeclList
      // Set the reachability of my ClassDeclList
      cl.setReachability();
    }
  }   

  // Declarations -------------------------------------------------------

  public static class ClassDecl extends Node {
    Id cid;		// class name
    Id pid;		// parent class name (could be null)
    VarDeclList vl;  	// fields (i.e. class variables)
    MethodDeclList ml;	// methods

    ClassDecl(Id ci, Id pi, VarDeclList avl, MethodDeclList aml) {
      cid=ci; pid=pi; vl=avl; ml=aml;
    }

    public void dump() { 
      DUMP(" ClassDecl "); DUMP(cid); DUMP(pid); DUMP("\n"); 
      Ast.tab = 2; DUMP(vl); DUMP(ml); 
    }


    // Call setReachability on this Class' MethodDeclList
    public void setReachability() {
      ml.setReachability();
    }

  }

  public static class MethodDecl extends Node {
    Type t;		// return type
    Id mid;		// method name
    FormalList fl;	// formal parameters
    VarDeclList vl;	// local variables
    StmtList sl;	// method body
    // notReachable indicates that this method has returned
    // (used for testing reachability)
    // It is initialized at the MethodDecl level as false
    // because when a method is instantiated, it has not yet returned.
    boolean notReachable = false; 

    MethodDecl(Type at, Id i, FormalList afl, VarDeclList avl, StmtList asl) {
      t=at; mid=i; fl=afl; vl=avl; sl=asl;
    }

    public void dump() { 
      DUMP("  MethodDecl "); DUMP(t); DUMP(mid); DUMP(fl); DUMP("\n");
      Ast.tab = 3; DUMP(vl); DUMP(sl);
    }
    public void setReachability() {
      // MethodDecl contains a StmtList
      // Set the reachability of my StmtList
      // pass in the initial value of notReachable: false
      sl.setReachability(notReachable);
    }

  }

  public static class VarDecl extends Node {
    Type t;	// variable type
    Id var;	// variable name
    Exp e;	// init expr (could be null)

    VarDecl(Type at, Id i, Exp ae) { t=at; var=i; e=ae; }

    public void dump() { 
      DUMP(Ast.tab, "VarDecl "); DUMP(t); DUMP(var); DUMP(e); DUMP("\n"); 
    }
  }

  public static class Formal extends Node {
    Type t;	// parameter type
    Id id;	// parameter name

    Formal(Type at, Id i) { t=at; id=i; }

    public void dump() { 
      DUMP("(Formal "); DUMP(t); DUMP(id); DUMP(") "); 
    }
  }

  // Types --------------------------------------------------------------

  public static abstract class Type extends Node {}

  public static class IntType extends Type {
    public void dump() { DUMP("(IntType) "); }
  }

  public static class BoolType extends Type {
    public void dump() { DUMP("(BoolType) "); }
  }

  public static class ArrayType extends Type {
    Type et;	// array element type

    ArrayType(Type t) { et=t; }

    public void dump() { 
      DUMP("(ArrayType "); DUMP(et); DUMP(") "); 
    }
  }

  public static class ObjType extends Type {
    Id cid;	// object's class name

    ObjType(Id i) { cid=i; }

    public void dump() { 	
      DUMP("(ObjType "); DUMP(cid); DUMP(") "); 
    }
  }

  // Statements ---------------------------------------------------------

  public static abstract class Stmt extends Node {
    public boolean reachable = true; // initial flag value choice is arbitrary

    // Set up an abstract method for setReachability()
    // It is passed a boolean, which indicated whether or not the method
    // has returned yet. It returns the same boolean, which is then passed
    // down the chain of statements.  Once the statement list contains
    // a Return statement, the boolean value is set to true.
    // Once true, all subsequent calls to a statement's setReachability()
    // method will set the reachable flag (boolean) to false.
    public abstract boolean setReachability(boolean notReachable) ;
  }

  public static class Block extends Stmt {
    StmtList sl; // statements

    Block(StmtList asl) { sl=asl; }

    public void dump() { 
      if (sl!=null) {
	DUMP(!reachable,Ast.tab, "{\n"); 
        Ast.tab++; sl.dump(); Ast.tab--;
	DUMP(!reachable,Ast.tab, "}\n"); 
      }
    }

    // A Block contains a Statement List. Call setReachability on it.
    public boolean setReachability(boolean notReachable) {
      return sl.setReachability(notReachable);
    }

  }

  public static class Assign extends Stmt {
    Exp lhs;	// lhs of assignment
    Exp rhs;	// rhs of assignment

    Assign(Exp e1, Exp e2) { lhs=e1; rhs=e2; }

    public void dump() { 
      DUMP(!reachable,Ast.tab, "Assign "); DUMP(lhs); DUMP(rhs); DUMP("\n"); 
    }

    public boolean setReachability(boolean notReachable)
    {
      // Set the reachable property of this statement
      reachable = reachable && !notReachable;
      return notReachable;
    }
  }

  public static class CallStmt extends Stmt {
    Exp obj;	  // class object
    Id mid;	  // method name
    ExpList args; // arguments

    CallStmt(Exp e, Id mi, ExpList el) { obj=e; mid=mi; args=el; }

    public void dump() { 
      DUMP(!reachable,Ast.tab, "CallStmt "); DUMP(obj); DUMP(mid); 
      args.dump(); DUMP("\n"); 
    }

    // Call statements are handled in the standard manner:
    // Just set reachable to true or false depending on what is 
    // passed in (notReachable).
    public boolean setReachability(boolean notReachable)
    {
      // Set the reachable property of this statement
      reachable = reachable && !notReachable;
      return notReachable;
    }
  }

  public static class If extends Stmt {
    Exp e;	// cond expr	       
    Stmt s1;	// then clause
    Stmt s2;	// else clause

    If(Exp ae, Stmt as1, Stmt as2) { e=ae; s1=as1; s2=as2; }

    public void dump() { 
      DUMP(!reachable,Ast.tab, "If "); DUMP(e); DUMP("\n"); 
      Ast.tab++; DUMP(s1); Ast.tab--;
      if (s2!=null) {
	DUMP(!reachable,Ast.tab, "Else\n");
	Ast.tab++; DUMP(s2); Ast.tab--;
      }
    }

    // An If statement can branch. Call setReachability on each
    // branch.
    public boolean setReachability(boolean notReachable) {
      boolean eBool;
      reachable = reachable && !notReachable;
      
      // Evaluate the expression for constant:
      // Instantiate eVal as the value of e.cval() so that we
      // don't have to traverse the tree every time we want to 
      // evaluate it.
      Object eVal = e.cval();
      
      // If the expression is a constant boolean, then evaluate the 
      // boolean for true/false.  If it is false, then s1 is unreachable.
      // If it is true, then s2 is unreachable.
      if (eVal instanceof Boolean) {
        eBool = (Boolean)eVal;
    	if (s1 != null)
          s1.setReachability(!eBool);
    	if (s2 != null)
    	  s2.setReachability(eBool);
      }
    
      // Now deal with Return statements causing unreachability.
      notReachable = s1.setReachability(notReachable);
      if (s2 != null)
        notReachable = s2.setReachability(notReachable);
      return notReachable;
    }


  }

  public static class While extends Stmt {
    Exp e;	// cond expr
    Stmt s;	// body

    While(Exp ae, Stmt as) { e=ae; s=as; }

    public void dump() { 
      DUMP(!reachable,Ast.tab, "While "); DUMP(e); DUMP("\n");
      Ast.tab++; DUMP(s); Ast.tab--;
    }

    // A While statement contains a statement. Call setReachability()
    // on its statement and return the value back up the chain.
    public boolean setReachability(boolean notReachable) {

      boolean eBool;

      // Evaluate the expression for constant:
      // Instantiate eVal as the value of e.cval() so that we
      // don't have to traverse the tree every time we want to 
      // evaluate it.
      Object eVal = e.cval();
      
      // Set the reachable property of this statement
      reachable = reachable && !notReachable;

      // If the expression is a constant boolean, then evaluate the 
      // boolean for true/false.  If it is false, then s is unreachable.
      if (eVal instanceof Boolean) {
        eBool = (Boolean)eVal;
    	if (s != null)
          s.setReachability(!eBool);
	  }
      return s.setReachability(notReachable);
    }


  }   

  public static class Print extends Stmt {
    Exp e;	// argument to print

    Print(Exp ae) { e=ae; }

    public void dump() { 
      DUMP(!reachable,Ast.tab, "Print "); DUMP(e); DUMP("\n"); 
    }
    // Print statements are handled in the standard manner:
    // Just set reachable to true or false depending on what is 
    // passed in (notReachable).
    public boolean setReachability(boolean notReachable)
    {
      // Set the reachable property of this statement
      reachable = reachable && !notReachable;
      return notReachable;
    }
  }

  public static class Return extends Stmt {
    Exp e;	// return-value expr

    Return(Exp ae) { e=ae; }

    public void dump() { 
      DUMP(!reachable,Ast.tab, "Return "); DUMP(e); DUMP("\n"); 
    }

    // A Return statement is where the action happens in
    // the setReachability methods. No matter what is passed in
    // it ALWAYS returns true. This is because whenever Return
    // is called, it means that the method is returning and all
    // subsequent statements in the method will be unreachable.
    public boolean setReachability(boolean notReachable)
    {
      // Set the reachable property of this statement
      reachable = reachable && !notReachable;
      return true;
    }
  }

  // Expressions --------------------------------------------------------

  public static abstract class Exp extends Node {
    public abstract Object cval();
  }

  public static enum BOP {
    ADD("+"), SUB("-"), MUL("*"), DIV("/"), AND("&&"), OR("||"),
    EQ("=="), NE("!="), LT("<"), LE("<="), GT(">"), GE(">="),
    UND("??");
    private String name; // binary operator name

    BOP(String n) { name = n; }

    public String toString() { return name; }
  }

  public static enum UOP {
    NEG("-"), NOT("!"), UND("?");
    private String name; // unary operator name

    UOP(String n) { name = n; }

    public String toString() { return name; }
  }

  public static class Binop extends Exp {
    BOP op;	// binary operator
    Exp e1;	// left operand
    Exp e2;	// right operand

    Binop(BOP o, Exp ae1, Exp ae2) { op=o; e1=ae1; e2=ae2; }

    public void dump() { 
      DUMP("(Binop " + op.toString() + " "); DUMP(e1); DUMP(e2); DUMP(") ");
    }

    public Object cval() {
      Object returnVal = null;
      Object expVal1 = e1.cval();
      Object expVal2 = e2.cval();
      if (expVal1 == null || expVal2 == null)
        return null;
      else if (expVal1 instanceof Integer && expVal2 instanceof Integer)
      {
        int e1Int = (Integer)expVal1;
        int e2Int = (Integer)expVal2;
        // INTEGER EXPRESSIONS
        if (op == BOP.ADD) {
          returnVal = (e1Int + e2Int);
        }
        else if (op == BOP.SUB) {
          returnVal = (e1Int - e2Int);
        }
        else if (op == BOP.MUL) {
          returnVal = (e1Int * e2Int);
        }
        else if (op == BOP.DIV) {
          returnVal = (e1Int / e2Int);
        }
        else if (op == BOP.EQ) {
          returnVal = (e1Int == e2Int);
        }
        else if (op == BOP.NE) {
          returnVal = (e1Int != e2Int);
        }
        else if (op == BOP.LT) {
          returnVal = (e1Int < e2Int);
        }
        else if (op == BOP.LE) {
          returnVal = (e1Int <= e2Int);
        }
        else if (op == BOP.GT) {
          returnVal = (e1Int > e2Int);
        }
        else if (op == BOP.GE) {
          returnVal = (e1Int >= e2Int);
        }
      }

      else if (expVal1 instanceof Boolean && expVal2 instanceof Boolean)
      {
        // BOOLEAN EXPRESSIONS
        boolean e1Bool = (Boolean)expVal1;
        boolean e2Bool = (Boolean)expVal2;
        if (op == BOP.AND) {
          returnVal = (e1Bool && e2Bool);
        }
        else if (op == BOP.OR) {
          returnVal = (e1Bool || e2Bool);
        }
      }
      return returnVal;
    }
  }

  public static class Unop extends Exp {
    UOP op;	// unary operator
    Exp e;	// operand

    Unop(UOP o, Exp ae) { op=o; e=ae; }

    public void dump() { 
      DUMP("(Unop " + op.toString() + " "); DUMP(e); DUMP(") ");
    }

    public Object cval() {
      Object returnVal = null;
      Object expVal = e.cval();
      if (expVal == null)
        return null;
      if (expVal instanceof Integer && op == UOP.NEG)
        returnVal = -(Integer)expVal;
      else if (expVal instanceof Boolean && op == UOP.NOT)
        returnVal = !(Boolean)expVal;
      return returnVal;
    }

  }

  public static class Call extends Exp {
    Exp obj;	  // class object
    Id mid;	  // method name
    ExpList args; // arguments

    Call(Exp e, Id mi, ExpList el) { obj=e; mid=mi; args=el; }

    public void dump() { 
      DUMP("(Call "); DUMP(obj); DUMP(mid); 
      DUMP(args); DUMP(") ");
    }

    public Object cval() { return null; }
  }

  public static class NewArray extends Exp {
    Type et;	// array element type
    int sz;	// array size

    NewArray(Type t, int i) { et=t; sz=i; }

    public void dump() { 
      DUMP("(NewArray "); DUMP(et); DUMP(sz); DUMP(") ");
    }

    public Object cval() { return null; }
  }

  public static class ArrayElm extends Exp {
    Exp ar; 	// array object
    Exp idx;	// element index

    ArrayElm(Exp e1, Exp e2) { ar=e1; idx=e2; }

    public void dump() { 
      DUMP("(ArrayElm "); DUMP(ar); DUMP(idx); DUMP(") ");
    }

    public Object cval() { return null; }
  }

  public static class NewObj extends Exp {
    Id cid;	  // class name
    ExpList args; // arguments to the constructor

    NewObj(Id id, ExpList el) { cid=id; args=el; }

    public void dump() { 
      DUMP("(NewObj "); DUMP(cid);
      DUMP(args); DUMP(") ");
    }

    public Object cval() { return null; }
  }

  public static class Field extends Exp {
    Exp obj;	// class object
    Id  var;	// field name

    Field(Exp e, Id v) { obj=e; var=v; }
    public void dump() { 
      DUMP("(Field "); DUMP(obj); DUMP(var); DUMP(") ");
    }

    public Object cval() { return null; }
  }

  public static class Id extends Exp {
    String s;	// Id name

    Id(String as) { s=as; }

    public void dump() { DUMP("(Id " + s + ") "); }

    public Object cval() { return null; }
  }

  public static class IntVal extends Exp {
    int i;	// integer value

    IntVal(int ai) { i=ai; }

    //    public String toString() { return "int"; }
    public void dump() { DUMP("(IntVal " + i + ") "); }

    public Object cval() { return i; }
  }

  public static class StrVal extends Exp {
    String s;	// string value

    StrVal(String as) { s=as; }
    public void dump() { DUMP("(StrVal \"" + s + "\") "); }

    public Object cval() { return null; }
  }

  public static class True extends Exp {
    public void dump() { DUMP("(True) "); }

    public Object cval() { return true; }
  }

  public static class False extends Exp {
    public void dump() { DUMP("(False) "); }

    public Object cval() { return false; }
  }
}
