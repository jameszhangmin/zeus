package com.tudou.core.zeus.cms

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.tudou.utils.json.JSONUtils
import kafka.utils.VerifiableProperties
import org.json.JSONObject


/**
 * Created by wanganqing on 2015/3/13.
 */
class ObjectDecoder[T](props: VerifiableProperties = null) extends scala.AnyRef with kafka.serializer.Decoder[T] {
  override def fromBytes(bytes: Array[Byte]): T = {
    val inArray = new ByteArrayInputStream(bytes)
    val inObject = new ObjectInputStream(inArray)
    val obj = inObject.readObject().asInstanceOf[T]
    inObject.close()
    inArray.close()
    obj
  }
}

class ObjectEncoder[T](props: VerifiableProperties = null) extends scala.AnyRef with kafka.serializer.Encoder[T] {
  override def toBytes(t: T): Array[Byte] = {
    val outArray = new ByteArrayOutputStream()
    val outObject = new ObjectOutputStream(outArray)
    outObject.writeObject(t)
    val byteArray: Array[Byte] = outArray.toByteArray
    outObject.close()
    outArray.close()
    byteArray
  }
}
class StringDecoder[T](props: VerifiableProperties = null) extends scala.AnyRef with kafka.serializer.Decoder[T] {
  override def fromBytes(bytes: Array[Byte]): T = {
//    val inArray = new ByteArrayInputStream(bytes)
//    val inObject = new ObjectInputStream(inArray)
//    val obj = inObject.readObject().asInstanceOf[T]
//    inObject.close()
//    inArray.close()
//    obj
    new String(bytes).asInstanceOf[T]
  }
}