package utils

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import java.util.concurrent.{ ExecutorService, Executors }
import scala.concurrent.{ ExecutionContext, ExecutionContextExecutor }

trait CommonSpec extends PlaySpec with ScalaFutures with MockitoSugar {
  val singleThreadPool: ExecutorService     = Executors.newSingleThreadExecutor()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(singleThreadPool)
}
