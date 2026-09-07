# -*- coding: utf-8 -*-
"""容器内 TCP 转发：0.0.0.0:80 -> host.docker.internal:5173
与 natapp 容器共享网络命名空间（--network container:pindan-tunnel），
使隧道 LocalAddr(127.0.0.1:80) 能到达宿主机的 Vite 前端 5173。
"""
import asyncio

SRC_PORT = int(__import__("os").environ.get("SRC_PORT", "80"))
DST_HOST = __import__("os").environ.get("DST_HOST", "host.docker.internal")
DST_PORT = int(__import__("os").environ.get("DST_PORT", "5173"))


async def pipe(reader, writer):
    try:
        while True:
            data = await reader.read(65536)
            if not data:
                break
            writer.write(data)
            await writer.drain()
    except Exception:
        pass
    finally:
        try:
            writer.close()
        except Exception:
            pass


async def handle(reader, writer):
    try:
        dst_reader, dst_writer = await asyncio.open_connection(DST_HOST, DST_PORT)
    except Exception as e:
        print("[proxy] connect fail:", e, flush=True)
        writer.close()
        return
    print("[proxy] forwarded", flush=True)
    await asyncio.gather(pipe(reader, dst_writer), pipe(dst_reader, writer))


async def main():
    server = await asyncio.start_server(handle, "0.0.0.0", SRC_PORT)
    print("[proxy] listening :%d -> %s:%d" % (SRC_PORT, DST_HOST, DST_PORT), flush=True)
    async with server:
        await server.serve_forever()


asyncio.run(main())
