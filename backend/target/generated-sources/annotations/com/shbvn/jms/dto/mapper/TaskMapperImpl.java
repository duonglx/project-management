package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.CommentResponse;
import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.Comment;
import com.shbvn.jms.model.Task;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T15:19:59+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    private final DatatypeFactory datatypeFactory;

    public TaskMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public TaskResponse toResponse(Task task) {
        if ( task == null ) {
            return null;
        }

        UserResponse assignee = null;
        List<CommentResponse> comments = null;
        String id = null;
        String projectId = null;
        String title = null;
        String description = null;
        String assigneeId = null;
        LocalDateTime dueDate = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        assignee = userMapper.toResponse( task.getAssignee() );
        comments = commentListToCommentResponseList( task.getComments() );
        id = task.getId();
        projectId = task.getProjectId();
        title = task.getTitle();
        description = task.getDescription();
        assigneeId = task.getAssigneeId();
        dueDate = xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( task.getDueDate() ) );
        createdAt = task.getCreatedAt();
        updatedAt = task.getUpdatedAt();

        String status = enumToString(task.getStatus());
        String type = enumToString(task.getType());
        String priority = enumToString(task.getPriority());

        TaskResponse taskResponse = new TaskResponse( id, projectId, title, description, status, type, priority, assigneeId, dueDate, createdAt, updatedAt, assignee, comments );

        return taskResponse;
    }

    private XMLGregorianCalendar localDateToXmlGregorianCalendar( LocalDate localDate ) {
        if ( localDate == null ) {
            return null;
        }

        return datatypeFactory.newXMLGregorianCalendarDate(
            localDate.getYear(),
            localDate.getMonthValue(),
            localDate.getDayOfMonth(),
            DatatypeConstants.FIELD_UNDEFINED );
    }

    private static LocalDateTime xmlGregorianCalendarToLocalDateTime( XMLGregorianCalendar xcal ) {
        if ( xcal == null ) {
            return null;
        }

        if ( xcal.getYear() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getMonth() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getDay() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getHour() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getMinute() != DatatypeConstants.FIELD_UNDEFINED
        ) {
            if ( xcal.getSecond() != DatatypeConstants.FIELD_UNDEFINED
                && xcal.getMillisecond() != DatatypeConstants.FIELD_UNDEFINED ) {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute(),
                    xcal.getSecond(),
                    Duration.ofMillis( xcal.getMillisecond() ).getNano()
                );
            }
            else if ( xcal.getSecond() != DatatypeConstants.FIELD_UNDEFINED ) {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute(),
                    xcal.getSecond()
                );
            }
            else {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute()
                );
            }
        }
        return null;
    }

    protected List<CommentResponse> commentListToCommentResponseList(List<Comment> list) {
        if ( list == null ) {
            return null;
        }

        List<CommentResponse> list1 = new ArrayList<CommentResponse>( list.size() );
        for ( Comment comment : list ) {
            list1.add( commentMapper.toResponse( comment ) );
        }

        return list1;
    }
}
