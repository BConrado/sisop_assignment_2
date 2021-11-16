public class process{
  private String name;
  private int size;
  private String op;
  private int partitionIndex;

  public int getPartitionIndex() {
    return this.partitionIndex;
  }

  public String getName() {
    return this.name;
  }

  public void setIndex(int index){
    this.partitionIndex = index;
  }

  public int getSize() {
    return this.size;
  }

  public String getOp() {
    return this.op;
  }
  

  public process(String name, int size, String op) {
    this.op = op;
    this.name = name;
    this.size = size;
    this.partitionIndex = -1;
  }
}