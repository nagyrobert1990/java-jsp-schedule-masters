package com.codecool.web.servlet;

import com.codecool.web.dao.TaskDao;
import com.codecool.web.dao.database.DatabaseTaskDao;
import com.codecool.web.exceptions.ServiceException;
import com.codecool.web.model.Task;
import com.codecool.web.model.User;
import com.codecool.web.service.TaskService;
import com.codecool.web.service.simple.SimpleTaskService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/protected/userTasks")

public class UserTasksServlet extends AbstractServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = getConnection(req.getServletContext())) {
            TaskDao taskDao = new DatabaseTaskDao(connection);
            TaskService taskService = new SimpleTaskService(taskDao);

            String userId = req.getParameter("userId");

            List<Task> tasks = taskService.getAllTasksByUserId(userId);

            sendMessage(resp, HttpServletResponse.SC_OK, tasks);
        } catch (SQLException e) {
            handleSqlError(resp, e);
        } catch (ServiceException e) {
            sendMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
