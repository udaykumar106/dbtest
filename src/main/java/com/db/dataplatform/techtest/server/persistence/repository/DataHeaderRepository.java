package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {
    @Transactional
    @Modifying
    @Query("update DataHeaderEntity e set e.blocktype = :type where e.name = :name")
    void updateBlocktypeByName(@Param("type") BlockTypeEnum type, @Param("name") String name);

}
