/**
 * 
 */
package mp4.util.atom;



public class CO64Atom extends LeafAtom {
  public CO64Atom() {
    super(new byte[]{'c','o','6','4'});
  }
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }
}