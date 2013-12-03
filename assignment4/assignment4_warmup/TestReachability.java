import java.io.*;

class TestReachability {
  public static void main(String [] args) {
    try {
      if (args.length == 1) {
	FileInputStream stream = new FileInputStream(args[0]);
	Ast.Program p = new AstParser(stream).Program();
	stream.close();
	p.setReachability();
	p.dump();
      } else {
	System.out.println("Need an AST file name as command-line argument.");
      }
    } catch (TokenMgrError e) {
      System.err.println(e.toString());
    } catch (ParseException e) {
      System.err.println(e.toString());
    } catch (IOException e) {
      System.err.println(e.toString());
    }     
  }
}