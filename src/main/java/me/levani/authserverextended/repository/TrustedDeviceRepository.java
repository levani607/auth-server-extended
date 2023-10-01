package me.levani.authserverextended.repository;

import me.levani.authorizationserver.model.enums.EntityStatus;
import me.levani.authserverextended.model.domain.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

    @Query("""
            select td from TrustedDevice td
            where td.deviceId = :deviceId
            and td.user.username = :userName
            and td.entityStatus=:status
                """)
    Optional<TrustedDevice> findByIdAndUserName(@Param("deviceId")String deviceId,
                                                @Param("userName")String userName,
                                                @Param("status")EntityStatus status);
}
