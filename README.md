=================Dibuat oleh=======================
1. Alvin Natawiguna 13512030
2. Kevin 13512096

=================Penginstallan=====================
1. Install Software Erlang otp_win64_18.1 - erlang 
2. Install Software RabbitMQ Server rabbitmq-server-3.5.5

=================Pengaktifan Jar===================
1. Masuk pada direktori ..\out\artifacts\
2. Jalankan code : java -cp PAtRAbbitMQ.jar ClientRabbit dengan menggunakan
command prompt pada direktori tersebut.

=================Penjelasan========================
1. Tidak terdapat file server untuk melakukan pemrosesan perintah dari
client yang dimasukkan. Setiap perintah yang dimasukkan akan diproses
langsung pada client.
2. Terdapat 2 class yang berfungsi untuk Consumer
(ReceiveLogsTopic) dan Publisher (EmitLogTopic)
3. Pada kelas EmitLogTopic akan disimpan channel yang di-join oleh client,
untuk digunakan pada basicPublish supaya dapat mengetahui routingKey
message yang akan dikirim melalui Exchange.
4. Pada kelas ReceiveLogsTopic, pada saat client join sebuah channel, akan
dilakukan binding pada queue yang digunakan client dengan menggunakan nama
channel sebagai bindingKey.
5. ReceiveLogsTopic mengimplementasi Runnable dan akan dijalankan pada
Thread terpisah untuk melakukan consume terdapat pesan-pesan yang masuk
pada quueue yang dibind oleh client(Channel yang dijoin client).

=================Tambahan Keterangan===============
-