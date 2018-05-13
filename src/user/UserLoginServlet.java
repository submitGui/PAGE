package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		String userID = request.getParameter("userID");
		String userPassword = request.getParameter("userPassword");
		if(userID == null || userID.equals("") || userPassword == null || userPassword.equals("")) { //�α��� ���н� ��µǴ� �޽��� ��系���� �Է�x
			request.getSession().setAttribute("messageType", "�����޽���");
			request.getSession().setAttribute("messageContent", "��� ������ �Է����ּ���");
			response.sendRedirect("login.jsp");//login�������� ������
			return;
		}
		int result = new UserDAO().login(userID, userPassword);
		if(result == 1) { //�α��� ������ ������ִ� �޽���
			request.getSession().setAttribute("userID", userID);
			request.getSession().setAttribute("messageType", "�����޽���");
			request.getSession().setAttribute("messageContent", "�α��ο� �����߽��ϴ�.");
			response.sendRedirect("index.jsp"); //index�������� ������
		}
		else if (result == 2) { //��й�ȣ Ʋ������ ������ �޽���
			request.getSession().setAttribute("messageType", "�����޽���");
			request.getSession().setAttribute("messageContent", "��й�ȣ�� �ٽ� Ȯ���ϼ���.");
			response.sendRedirect("login.jsp"); //index�������� ������
		}
		else if (result == 0) { //���̵� �������� ������ ������ �޽���
			request.getSession().setAttribute("messageType", "�����޽���");
			request.getSession().setAttribute("messageContent", "���̵� �������� �ʽ��ϴ�.");
			response.sendRedirect("login.jsp"); //index�������� ������
		}
		else if (result == -1) { //�����ͺ��̽� ����
			request.getSession().setAttribute("messageType", "�����޽���");
			request.getSession().setAttribute("messageContent", "�����ͺ��̽� ������ �߻��߽��ϴ�.");
			response.sendRedirect("login.jsp"); //index�������� ������
		}
	}
}
