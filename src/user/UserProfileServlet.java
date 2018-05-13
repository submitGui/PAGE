package user;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		MultipartRequest multi = null; //�ʱ�ȭ
		int fileMaxSize = 10 * 1024 * 1024; //������ �ִ�ũ�⼳��
		String savePath = request.getRealPath("/upload/").replaceAll("\\\\", "/");//���� ������Ͽ��� ���ε� ������ ���ε��Ҽ� �ֵ��� ��μ���
		try {
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
		} catch (Exception e) {
			request.getSession().setAttribute("messageType", "�����޽���");
			request.getSession().setAttribute("messageContent", "����ũ��� 10MB�� ���� �� �����ϴ�.");
			response.sendRedirect("profileUpdate.jsp");
			return;
		}
		String userID = multi.getParameter("userID");//������� userID ���� �޾ƿ´�
		HttpSession session = request.getSession();
		if(!userID.equals((String) session.getAttribute("userID"))) {
			session.setAttribute("messageType", "�����޽���");
			session.setAttribute("messageContent", "������ �� �����ϴ�.");
			response.sendRedirect("index.jsp");
			return;
		}
		String fileName = "";
		File file = multi.getFile("userProfile");
		if(file != null) {
			String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);//���帶���������� Ȯ���� Ȯ��
			if(ext.equals("jpg") || ext.equals("png") || ext.equals("gif")) {
				String prev = new UserDAO().getUser(userID).getUserProfile();//������� �⺻������ �����ͼ� profile�� ������
				File prevFile = new File(savePath + "/" + prev);//������ ��θ� ã�Ƽ� ������ ����
				if(prevFile.exists()) {
					prevFile.delete();
				}
				fileName = file.getName(); //�������� ���� �̸��� ������� ������ ������ �̸����� ���
			} else {
				if(file.exists()) {
					file.delete();
				}
				session.setAttribute("messageType", "�����޽���");
				session.setAttribute("messageContent", "�̹��� ���ϸ� ���ε� �����մϴ�.");
				response.sendRedirect("profileUpdate.jsp");
				return;
			}
		}
		new UserDAO().profile(userID, fileName);//������ ���̽��� �����ؼ� ������� ������ ������ ������Ʈ����
		session.setAttribute("messageType", "�����޽���");
		session.setAttribute("messageContent", "���������� �������� ����Ǿ����ϴ�.");
		response.sendRedirect("index.jsp");
		return;
	}

}
