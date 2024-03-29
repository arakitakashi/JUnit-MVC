package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {
    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute("insert into student(id, firstName, lastName, email_address)" +
                "values(1, 'Eric', 'Roby', 'eric.roby@luv2code_school.com')");
    }

    @Test
    public void createStudentService() {
        studentAndGradeService.createStudent("Chad", "Darby", "chad.darby@luv2code_school.com");

        CollegeStudent student = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");

        assertEquals("chad.darby@luv2code_school.com", student.getEmailAddress(), "find by email");
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentAndGradeService.checkIfStudentIsNull(1));
        assertFalse(studentAndGradeService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        assertTrue(deletedCollegeStudent.isPresent(), "Return True");
        studentAndGradeService.deleteStudent(1);
        deletedCollegeStudent = studentDao.findById(1);
        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
    }

    @Sql("/insertData.sql") // テストメソッド実行前にsqlの実行*BeforeEachと合わせて実行
    @Test
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentAndGradeService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
    }
}
