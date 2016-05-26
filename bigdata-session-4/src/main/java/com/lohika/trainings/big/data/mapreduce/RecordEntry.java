package com.lohika.trainings.big.data.mapreduce;

/**
 * @author eim
 * @since 2016-05-26
 */
public class RecordEntry {

  private int id;
  private String name;

  public RecordEntry() {
  }

  public RecordEntry(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
