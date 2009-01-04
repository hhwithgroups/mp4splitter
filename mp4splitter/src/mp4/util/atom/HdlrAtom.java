/**
 * 
 */
package mp4.util.atom;

/**
 * The media handler type atom
 */
public class HdlrAtom extends LeafAtom {
  private static final int COMPONENT_SUBTYPE_OFFSET = 8;
  
  /**
   * Construct an empty hdlr atom.
   */
  public HdlrAtom() {
    super(new byte[]{'h','d','l','r'});
  }
  
  /**
   * Copy constructor. Performs a deep copy
   * @param old the version to copy
   */
  public HdlrAtom(HdlrAtom old) {
    super(old);
  }
  
  public String getComponentSubtype() {
    return new String(data.getData(COMPONENT_SUBTYPE_OFFSET, COMPONENT_SUBTYPE_OFFSET + ATOM_WORD));
  }
  
  /**
   * Cut the hdlr atom, which does not change the contents.  This method
   * returns a copy.
   * @return a copy of the hdlr atom
   */
  public HdlrAtom cut() {
    return new HdlrAtom(this);
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}