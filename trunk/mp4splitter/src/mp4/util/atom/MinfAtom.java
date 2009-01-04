/**
 * 
 */
package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;

/**
 * The media information container atom.
 */
public class MinfAtom extends ContainerAtom {
  // video media header atom (if video)
  private VmhdAtom vmhd;
  // sound media header atom (if sound)
  private SmhdAtom smhd;
  // the data information atom
  private DinfAtom dinf;
  // the sample table atom
  private StblAtom stbl;
  
  /**
   * Construct an empty minfo atom
   */
  public MinfAtom() {
    super(new byte[]{'m','i','n','f'});
  }
  
  /**
   * Copy constructor.  Performs a deep copy.
   * @param old the version to copy
   */
  public MinfAtom(MinfAtom old) {
    super(old);
    if (old.vmhd != null) {
      vmhd = new VmhdAtom(old.vmhd);
    }
    if (old.smhd != null) {
      smhd = new SmhdAtom(old.smhd);
    }
    dinf = new DinfAtom(old.dinf);
    stbl = new StblAtom(old.stbl);
  }
  
  public VmhdAtom getVmhd() {
    return vmhd;
  }
  
  public SmhdAtom getSmhd() {
    return smhd;
  }
  
  public DinfAtom getDinf() {
    return dinf;
  }
  
  public StblAtom getStbl() {
    return stbl;
  }
  
  /**
   * Add an atom to the minf atom container.  Throws a run-time exception 
   * if the the atom is not contained in a minf container.
   * @param child the atom to add
   */
  @Override
  public void addChild(Atom child) {
    if (child instanceof VmhdAtom) {
      vmhd = (VmhdAtom) child;
    }
    else if (child instanceof SmhdAtom) {
      smhd = (SmhdAtom) child;
    }
    else if (child instanceof DinfAtom) {
      dinf = (DinfAtom) child;
    }
    else if (child instanceof StblAtom) {
      stbl = (StblAtom) child;
    }
    else {
      throw new AtomError("Can't add " + child + " to minf");
    }
  }

  /**
   * Recompute the size of the minf atom, which needs to be done if
   * any of the child atom sizes have changed.
   */
  @Override
  protected void recomputeSize() {
    long newSize = dinf.size() + stbl.size();
    if (vmhd != null) {
      newSize += vmhd.size();
    }
    if (smhd != null) {
      newSize += smhd.size();
    }
    setSize(newSize);
    
  }

  /**
   * Cut the atom at the specified time.
   * @param time the time
   * @return a new minf atom
   */
  public MinfAtom cut(long time) {
    MinfAtom cutMinf = new MinfAtom();
    if (vmhd != null) {
      cutMinf.vmhd = vmhd.cut();
    }
    if (smhd != null) {
      cutMinf.smhd = smhd.cut();
    }
    cutMinf.dinf = dinf.cut();
    cutMinf.stbl = stbl.cut(time);
    cutMinf.recomputeSize();
    return cutMinf;
  }

  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }

  /**
   * Write the minf atom data to the specified output
   * @param out where the data goes
   * @throws IOException if there is an error writing the data
   */
  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    if (vmhd != null) {
      vmhd.writeData(out);
    }
    if (smhd != null) {
      smhd.writeData(out);
    }
    dinf.writeData(out);
    stbl.writeData(out);
  }
}