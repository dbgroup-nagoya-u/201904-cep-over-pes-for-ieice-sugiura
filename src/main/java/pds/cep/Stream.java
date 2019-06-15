package pds.cep;

import java.util.ArrayList;
import java.util.List;

/**
 * Stream
 */
public abstract class Stream {

  protected List<Event> stream;

  private int offset;

  public Stream(int streamSize) {
    this.stream = new ArrayList<>(streamSize);
    this.resetOffset();
  }

  /**
   * 先頭のイベントを出力しオフセットを1進める．
   *
   * @return
   */
  public Event pull() {
    return this.stream.get(offset++);
  }

  /**
   * ストリームのオフセットをリセットし先頭（=Stream[0]）に戻す．
   */
  public void resetOffset() {
    this.offset = 0;
  }

  /**
   * オフセットが終端に辿り着いていなければtrueを返す．
   *
   * @return
   */
  public boolean hasNext() {
    return this.offset < this.stream.size();
  }
}