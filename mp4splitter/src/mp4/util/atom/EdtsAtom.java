/**
 * 
 */
package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;

/**
 * The edit list container atom.
 */
public class EdtsAtom extends ContainerAtom {
  // the edit list (optional)
  private ElstAtom elst;
  
  /**
   * Construct an empty edit list container atom.
   */
  public EdtsAtom() {
    super(new byte[]{'e','d','t','s'});
  }
  
  /**
   * Copy constructor.  Performs a deep copy.
   * @param old the verison to copy
   */
  public EdtsAtom(EdtsAtom old) {
    super(old);
    if (old.elst != null) {
      elst = new ElstAtom(old.elst);
    }
  }
  
  public ElstAtom getElst() { 
    return elst; 
  }

  /**
   * Add an elst atom to the edts atom.  If it's not an elst atom,
   * then throw a run-time exception.
   * @param child the elst atom to add
   */
  @Override
  public void addChild(Atom child) {
    if (child instanceof ElstAtom) {
      elst = (ElstAtom) child;
    }
    else {
      throw new AtomError("Can't add " + child + " to edts");
    }
  }

  /**
   * Recompute the size of the edts atom, which is a noop since the contents
   * do not change.
   */
  @Override
  protected void recomputeSize() {
    setSize(elst.size());    
  }

  /**
   * Cut the edit list atom, which does not change the contents.  This method
   * returns a copy.
   * @return a copy of the edit list atom
   */
  public EdtsAtom cut() {
    return new EdtsAtom(this);
  }

  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }

  /**
   * Write the edts atom data to the specified output
   * @param out where the data goes
   * @throws IOException if there is an error writing the data
   */  
  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    elst.writeData(out);
  }
}