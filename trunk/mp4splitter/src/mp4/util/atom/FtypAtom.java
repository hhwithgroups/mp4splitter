/**
 * 
 */
package mp4.util.atom;

/**
 * The file type atom.  This is the first atom in the mp4 stream.
 */
public class FtypAtom extends LeafAtom {
  
  public static final int MAJOR_BRAND_OFFSET = 8;
  
  public FtypAtom() {
    super(new byte[]{'f','t','y','p'});
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }
  
  public byte[] getMajorBrand() {
    return data.getData(MAJOR_BRAND_OFFSET, MAJOR_BRAND_OFFSET + ATOM_WORD);
  }
}