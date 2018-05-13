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
		MultipartRequest multi = null; //초기화
		int fileMaxSize = 10 * 1024 * 1024; //파일의 최대크기설정
		String savePath = request.getRealPath("/upload/").replaceAll("\\\\", "/");//실제 경로파일에서 업로드 폴더로 업로드할수 있도록 경로설정
		try {
			multi = new MultipartRequest(request, savePath, fileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
		} catch (Exception e) {
			request.getSession().setAttribute("messageType", "오류메시지");
			request.getSession().setAttribute("messageContent", "파일크기는 10MB를 넘을 수 없습니다.");
			response.sendRedirect("profileUpdate.jsp");
			return;
		}
		String userID = multi.getParameter("userID");//사용자의 userID 값을 받아온다
		HttpSession session = request.getSession();
		if(!userID.equals((String) session.getAttribute("userID"))) {
			session.setAttribute("messageType", "오류메시지");
			session.setAttribute("messageContent", "접근할 수 없습니다.");
			response.sendRedirect("index.jsp");
			return;
		}
		String fileName = "";
		File file = multi.getFile("userProfile");
		if(file != null) {
			String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);//가장마지막파일의 확장자 확인
			if(ext.equals("jpg") || ext.equals("png") || ext.equals("gif")) {
				String prev = new UserDAO().getUser(userID).getUserProfile();//사용자의 기본정보를 가져와서 profile을 가져옴
				File prevFile = new File(savePath + "/" + prev);//파일의 경로를 찾아서 파일을 지움
				if(prevFile.exists()) {
					prevFile.delete();
				}
				fileName = file.getName(); //그파일의 실제 이름을 사용자의 프로필 사진의 이름으로 사용
			} else {
				if(file.exists()) {
					file.delete();
				}
				session.setAttribute("messageType", "오류메시지");
				session.setAttribute("messageContent", "이미지 파일만 업로드 가능합니다.");
				response.sendRedirect("profileUpdate.jsp");
				return;
			}
		}
		new UserDAO().profile(userID, fileName);//데이터 베이스와 연동해서 사용자의 프로필 파일을 업데이트해줌
		session.setAttribute("messageType", "성공메시지");
		session.setAttribute("messageContent", "성공적으로 프로필이 변경되었습니다.");
		response.sendRedirect("index.jsp");
		return;
	}

}
