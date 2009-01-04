/**
 * 
 */
package mp4.util.atom;


public class GmhdAtom extends LeafAtom {
    public GmhdAtom() {
      super(new byte[]{'g','m','h','d'});
    }
    @Override
    public void accept(AtomVisitor v) throws AtomException {
      v.visit(this); 
    }
 }