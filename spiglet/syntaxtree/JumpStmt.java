//
// Generated by JTB 1.3.2
//

package spiglet.syntaxtree;

/**
 * Grammar production:
 * f0 -> "JUMP"
 * f1 -> Label()
 */
public class JumpStmt implements Node {
   public NodeToken f0;
   public Label f1;

   public JumpStmt(NodeToken n0, Label n1) {
      f0 = n0;
      f1 = n1;
   }

   public JumpStmt(Label n0) {
      f0 = new NodeToken("JUMP");
      f1 = n0;
   }

   public void accept(spiglet.visitor.Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(spiglet.visitor.GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(spiglet.visitor.GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(spiglet.visitor.GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}

