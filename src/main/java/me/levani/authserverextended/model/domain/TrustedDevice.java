package me.levani.authserverextended.model.domain;

import jakarta.persistence.*;
import lombok.*;
import me.levani.authorizationserver.model.domain.RealmUser;
import me.levani.authorizationserver.model.enums.EntityStatus;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

@Entity
@Table(schema = "extended_logic",name = "trusted_device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrustedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RealmUser user;

    @Column(name = "device_id")
    private String deviceId;

    @Lob
    @Lazy
    @Column(name = "public_key")
    private byte[] publicKey;

    public EntityStatus entityStatus;

}
