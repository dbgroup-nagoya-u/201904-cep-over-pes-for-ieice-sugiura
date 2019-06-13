package pds.cep;

/**
 * Stream
 */
public interface Stream {

  /**
   * 先頭のイベントを出力しオフセットを1進める．
   *
   * @return
   */
  public Event pull();

  /**
   * ストリームのオフセットをリセットし先頭（=Stream[0]）に戻す．
   */
  public void resetOffset();

  /**
   * オフセットが終端に辿り着いていなければtrueを返す．
   *
   * @return
   */
  public boolean hasNext();
}