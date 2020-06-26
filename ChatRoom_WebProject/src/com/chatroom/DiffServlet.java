package com.chatroom;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class DiffServlet
 */
@WebServlet("/DiffServlet")
public class DiffServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static JSONArray messageList = new JSONArray();
    //易出现空指针异常
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DiffServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    public void init()throws ServletException{
    	if(messageList.isEmpty()) {
			JSONObject first = new JSONObject();
			first.put("name", "Server");
			first.put("message", "Server Gets Ready");
			messageList.add(first);
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write(messageList.toString());
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		String username = request.getParameter("name");
		String message = request.getParameter("message");
		JSONObject obj = new JSONObject();
		obj.put("name", username);
		obj.put("message", message);
		messageList.add(obj);
	}
}
