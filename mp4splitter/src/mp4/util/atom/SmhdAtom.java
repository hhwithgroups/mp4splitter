/**
 * 
 */
package mp4.util.atom;

/**
 * The sound media information header atom.
 */
public class SmhdAtom extends LeafAtom {
  
  public SmhdAtom() {
    super(new byte[]{'s','m','h','d'});
  }
  
  /**
   * Copy constructor. Performs a deep copy.
   * @param old the old version of the object
   */
  public SmhdAtom(SmhdAtom old) {
    super(old);
  }
  
  /**
   * Cutting this method does not do anything to the contents, so
   * just return a new copy.
   * @return a copy of the smhd atom
   */
  public SmhdAtom cut() {
    return new SmhdAtom(this);
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}