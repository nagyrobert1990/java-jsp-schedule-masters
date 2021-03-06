package com.codecool.web.service.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codecool.web.dao.ColumnDao;
import com.codecool.web.dao.ScheduleDao;
import com.codecool.web.dao.SlotDao;
import com.codecool.web.exceptions.EmptyFieldException;
import com.codecool.web.exceptions.ServiceException;
import com.codecool.web.model.Column;
import com.codecool.web.model.Schedule;
import com.codecool.web.service.ScheduleService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleScheduleService implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleScheduleService.class);

    private final ScheduleDao scheduleDao;
    private final ColumnDao columnDao;
    private final SlotDao slotDao;

    public SimpleScheduleService(ScheduleDao scheduleDao, ColumnDao columnDao, SlotDao slotDao) {
        this.scheduleDao = scheduleDao;
        this.columnDao = columnDao;
        this.slotDao = slotDao;
    }

    @Override
    public Schedule getById(int id) throws SQLException, ServiceException {
        if (scheduleDao.findById(id) == null) {
            logger.error("No schedule found by ID");
            throw new ServiceException("No schedules with this id");
        }
        logger.info("Schedule returned by ID");
        return scheduleDao.findById(id);
    }

    @Override
    public List<Schedule> getSchedulesByIds(List<Integer> scheduleIds) throws SQLException, ServiceException {
        List<Schedule> schedules = new ArrayList<>();
        for (int scheduleId : scheduleIds) {
            schedules.add(getById(scheduleId));
        }
        return schedules;
    }

    @Override
    public Schedule addSchedule(int userId, String name, int amountOfColumns) throws EmptyFieldException, ServiceException, SQLException {
        if (name.equals("")) {
            logger.error("Field(s) left empty");
            throw new ServiceException(new EmptyFieldException("Fill all fields"));
        }
        if (amountOfColumns < 1 || amountOfColumns > 7) {
            logger.error("Column number not between 1 and 7");
            throw new ServiceException("Invalid amount of columns.Enter number between 1 and 7");
        }
        Schedule schedule = scheduleDao.insertSchedule(userId, name);

        for (int i = 0; i < amountOfColumns; i++) {
            Column column = columnDao.insert(schedule.getId(), "Enter text");
            for (int j = 7; j < 19; j++) {
                String timeRange = j + "-" + (j + 1);
                slotDao.insertNewSlot(column.getId(), timeRange);
            }
        }
        logger.info("New schedule added");
        return schedule;
    }

    @Override
    public void editName(int id, String name) throws SQLException, ServiceException, EmptyFieldException {
        if (scheduleDao.findById(id) == null) {
            logger.error("No schedule by ID");
            throw new ServiceException("No schedule with this id");
        }
        if (name.equals("")) {
            logger.error("Field(s) left empty");
            throw new ServiceException(new EmptyFieldException("Fill all fields"));
        }
        logger.info("Schedule name edited");
        scheduleDao.updateName(id, name);
    }

    @Override
    public List<Schedule> getSchedulesByUserId(int userId) throws SQLException {
        logger.info("Schedules returned by User ID");
        return scheduleDao.findSchedulesByUserId(userId);
    }

    @Override
    public List<Schedule> getSchedulesByUserId(String userId) throws SQLException, ServiceException {
        try {
            logger.info("Schedules returned by User ID");
            return scheduleDao.findSchedulesByUserId(Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            logger.error("Parameter to 'updateContent' method must be int");
            throw new ServiceException("Must be a number");
        } catch (IllegalArgumentException e) {
            logger.error("Parameter to 'updateContent' method must be int");
            throw new ServiceException("Illegal argument, must be number");
        }
    }

    @Override
    public void removeSchedule(int id) throws SQLException, ServiceException {
        if (scheduleDao.findById(id) == null) {
            logger.error("No schedule found by ID");
            throw new ServiceException("No schedule with this id");
        }
        logger.info("Schedule removed");
        scheduleDao.deleteSchedule(id);
    }

    @Override
    public int getColumnNumber(int id) throws SQLException {
        logger.info("Column number returned by ID");
        return scheduleDao.getColumnNumber(id);
    }

    @Override
    public int getScheduleIdByTaskId(String taskId) throws SQLException, ServiceException {
        try {
            return scheduleDao.findScheduleIdByTaskId(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            logger.error("Parameter to 'updateContent' method must be int");
            throw new ServiceException("Must be a number");
        } catch (IllegalArgumentException e) {
            logger.error("Parameter to 'updateContent' method must be int");
            throw new ServiceException("Illegal argument, must be number");
        }
    }

    @Override
    public List<Integer> getScheduleIdsByTaskId(String taskId) throws SQLException, ServiceException {
        try {
            return scheduleDao.findScheduleIdsByTaskId(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            logger.error("Parameter to 'updateContent' method must be int");
            throw new ServiceException("Must be a number");
        } catch (IllegalArgumentException e) {
            logger.error("Parameter to 'updateContent' method must be int");
            throw new ServiceException("Illegal argument, must be number");
        }
    }

    @Override
    public Schedule publishSchedule(int id) throws SQLException {
        logger.info("Publishing schedule with ID:{}",id);
        return scheduleDao.insertScheduleToPublished(id);
    }

    @Override
    public void unPublishSchedule(int id) throws SQLException {
        logger.info("Unpublishing schedule with ID:{}",id);
        scheduleDao.removeScheduleFromPublished(id);
    }

    @Override
    public boolean isSchedulePublished(int id) throws SQLException {
        logger.info("Checking if schedule with ID:{} is published",id);
        return scheduleDao.findPublished(id);
    }

    @Override
    public String encrypt(Long id) {
        Long x;
        x = (id * 1708159939L) % 2176782336L;
        return String.format("%6s",Long.toString(x, 36).toUpperCase()).replace(' ','0');
    }

    @Override
    public int decrypt(String s) {
        int id =Math.toIntExact((Long.valueOf(s, 36) * 1553655019L) % 2176782336L);
        return id;
    }
}
