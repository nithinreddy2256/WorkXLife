package com.workxlife.employee_service.repository;

import com.workxlife.employee_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE LOWER(e.email) = LOWER(:email)")
    Optional<Employee> findByEmail(@Param("email") String email);


    // Search employees by first name (case-insensitive)
    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    List<Employee> findByFirstName(@Param("firstName") String firstName);

    // Search employees by department
    List<Employee> findByDepartmentIgnoreCase(String department);

    Page<Employee> findAll(Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Employee> findBySkillKeyword(@Param("skill") String skill);

    List<Employee> findByExperienceYearsGreaterThanEqual(int years);
}
