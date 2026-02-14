package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.ProjectMemberResponse;
import com.shbvn.jms.dto.response.ProjectResponse;
import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.model.Project;
import com.shbvn.jms.model.ProjectMember;
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
    date = "2026-02-13T16:54:26+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ProjectMemberMapper projectMemberMapper;
    private final DatatypeFactory datatypeFactory;

    public ProjectMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public ProjectResponse toResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        List<TaskResponse> tasks = null;
        List<ProjectMemberResponse> members = null;
        String id = null;
        String name = null;
        String description = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String teamLead = null;
        String workspaceId = null;
        Integer progress = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        tasks = taskListToTaskResponseList( project.getTasks() );
        members = projectMemberListToProjectMemberResponseList( project.getMembers() );
        id = project.getId();
        name = project.getName();
        description = project.getDescription();
        startDate = xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( project.getStartDate() ) );
        endDate = xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( project.getEndDate() ) );
        teamLead = project.getTeamLead();
        workspaceId = project.getWorkspaceId();
        progress = project.getProgress();
        createdAt = project.getCreatedAt();
        updatedAt = project.getUpdatedAt();

        String priority = enumToString(project.getPriority());
        String status = enumToString(project.getStatus());

        ProjectResponse projectResponse = new ProjectResponse( id, name, description, priority, status, startDate, endDate, teamLead, workspaceId, progress, createdAt, updatedAt, tasks, members );

        return projectResponse;
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

    protected List<TaskResponse> taskListToTaskResponseList(List<Task> list) {
        if ( list == null ) {
            return null;
        }

        List<TaskResponse> list1 = new ArrayList<TaskResponse>( list.size() );
        for ( Task task : list ) {
            list1.add( taskMapper.toResponse( task ) );
        }

        return list1;
    }

    protected List<ProjectMemberResponse> projectMemberListToProjectMemberResponseList(List<ProjectMember> list) {
        if ( list == null ) {
            return null;
        }

        List<ProjectMemberResponse> list1 = new ArrayList<ProjectMemberResponse>( list.size() );
        for ( ProjectMember projectMember : list ) {
            list1.add( projectMemberMapper.toResponse( projectMember ) );
        }

        return list1;
    }
}
