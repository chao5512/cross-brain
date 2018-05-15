/**
  * Created by 冯刚 on 2018/5/15.
  */
class HelloWordScala{


  def helloWord: Unit ={
    println("HelloWord!")
  }


}

object HelloWordScala{

  def main(args: Array[String]) {
    println("HelloWord!")
    val h = new HelloWordScala

    h.helloWord

  }

}
