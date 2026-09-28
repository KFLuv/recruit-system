package com.recruitment.repository;

import com.recruitment.entity.Candidate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
   List<Candidate> findByStatus(String status);

   List<Candidate> findByRealNameContaining(String name);

   List<Candidate> findByEducation(String education);

   @Query("SELECT c FROM Candidate c WHERE c.realName LIKE %:keyword% OR c.email LIKE %:keyword% OR c.phone LIKE %:keyword%")
   List<Candidate> searchCandidates(@Param("keyword") String keyword);

   Candidate findByCandidateNo(String candidateNo);
}
