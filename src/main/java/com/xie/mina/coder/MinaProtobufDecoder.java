package com.xie.mina.coder;

import com.xie.model.bean.StudentMsg;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * @Author xie
 * @Date 17/2/9 下午2:28.
 */
public class MinaProtobufDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        // 如果没有接收完Header部分（4字节），直接返回false
        if (in.remaining() < 4) {
            return false;
        } else {

            // 标记开始位置，如果一条消息没传输完成则返回到这个位置
            in.mark();

            // 读取header部分，获取body长度
            int bodyLength = in.getInt();

            // 如果body没有接收完整，直接返回false
            if (in.remaining() < bodyLength) {
                in.reset(); // IoBuffer position回到原来标记的地方
                return false;
            } else {
                byte[] bodyBytes = new byte[bodyLength];
                in.get(bodyBytes); // 读取body部分
                StudentMsg.Student student = StudentMsg.Student.parseFrom(bodyBytes); // 将body中protobuf字节码转成Student对象
                out.write(student); // 解析出一条消息
                return true;
            }
        }
    }
}