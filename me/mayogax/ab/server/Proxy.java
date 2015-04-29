/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014,XiaoYu (Gary) Ge, Stephen Gould,Jochen Renz
 **  Sahan Abeyasinghe, Jim Keys,   Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 **To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/

package me.mayogax.ab.server;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

import me.mayogax.ab.server.proxy.message.ProxyClickMessage;
import me.mayogax.ab.server.proxy.message.ProxyDragMessage;
import me.mayogax.ab.server.proxy.message.ProxyMouseWheelMessage;
import me.mayogax.ab.server.proxy.message.ProxyScreenshotMessage;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/* Proxy ------------------------------------------------------------------ */

public class Proxy extends WebSocketServer {
	private Long id = 0L;
	private HashMap<Long, ProxyResult<?>> results = new HashMap<Long, ProxyResult<?>>();

	private class ProxyResult<T> {
		public ProxyMessage<T> message;
		public SynchronousQueue<Object> queue = new SynchronousQueue<Object>();
	}

	public Proxy(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public Proxy(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		//System.out.println(conn.getRemoteSocketAddress().toString());
		onOpen();
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		onClose();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		JSONArray j = (JSONArray) JSONValue.parse(message);
		Long id = (Long) j.get(0);
		JSONObject data = (JSONObject) j.get(1);

		ProxyResult<?> result = results.get(id);

		if (result != null) {
			results.remove(id);
			try {
				result.queue.put(result.message.gotResponse(data));
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T send(ProxyScreenshotMessage proxyScreenshotMessage) {
		//Long t1 = System.nanoTime();
		JSONArray a = new JSONArray();
		a.add(id);
		a.add(proxyScreenshotMessage.getMessageName());
		a.add(proxyScreenshotMessage.getJSON());

		ProxyResult<T> result = new ProxyResult<T>();
		result.message = (ProxyMessage<T>) proxyScreenshotMessage;
		results.put(id, result);

		for (WebSocket conn : connections()) {	
			conn.send(a.toJSONString());
		}



		id++;

		try {

			return (T)result.queue.take();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return null;
	}

	public void onOpen() { }

	public void onClose() { }

	public void waitForClients(int numClients) {
		while (connections().size() < numClients) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public <T> T send(ProxyClickMessage proxyClickMessage) {
		//Long t1 = System.nanoTime();
		JSONArray a = new JSONArray();
		a.add(id);
		a.add(proxyClickMessage.getMessageName());
		a.add(proxyClickMessage.getJSON());

		ProxyResult<T> result = new ProxyResult<T>();
		result.message = (ProxyMessage<T>) proxyClickMessage;
		results.put(id, result);

		for (WebSocket conn : connections()) {	
			conn.send(a.toJSONString());
		}
		id++;

		try {
			return (T)result.queue.take();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return null;
	}

	public <T> T send(ProxyDragMessage proxyDragMessage) {
		//Long t1 = System.nanoTime();
				JSONArray a = new JSONArray();
				a.add(id);
				a.add(proxyDragMessage.getMessageName());
				a.add(proxyDragMessage.getJSON());

				ProxyResult<T> result = new ProxyResult<T>();
				result.message = (ProxyMessage<T>) proxyDragMessage;
				results.put(id, result);

				for (WebSocket conn : connections()) {	
					conn.send(a.toJSONString());
				}
				id++;

				try {
					return (T)result.queue.take();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				return null;
	}

	public <T> T send(ProxyMouseWheelMessage proxyMouseWheelMessage) {
		//Long t1 = System.nanoTime();
		JSONArray a = new JSONArray();
		a.add(id);
		a.add(proxyMouseWheelMessage.getMessageName());
		a.add(proxyMouseWheelMessage.getJSON());

		ProxyResult<T> result = new ProxyResult<T>();
		result.message = (ProxyMessage<T>) proxyMouseWheelMessage;
		results.put(id, result);

		for (WebSocket conn : connections()) {	
			conn.send(a.toJSONString());
		}
		id++;

		try {
			return (T)result.queue.take();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return null;
	}
}
