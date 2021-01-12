package io.jzheaux.springsecurity.resolutions;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResolutionRepository extends CrudRepository<Resolution, UUID> {
	@Modifying
	@Query("UPDATE Resolution SET text = :text WHERE id = :id AND owner = ?#{authentication.name}")
	void revise(@Param("id") UUID id,@Param("text") String text);

	@Modifying
	@Query("UPDATE Resolution SET completed = 1 WHERE id = :id AND owner = ?#{authentication.name}")
	void complete(@Param("id") UUID id);
}
