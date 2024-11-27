package com.mahim.shopme.common.entity;

import com.mahim.shopme.common.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "order_track")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class OrderTrack extends ParentEntity {

    @Column(length = 256)
    private String notes;

    private Date updatedTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public String getUpdatedTimeOnForm() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(updatedTime);
    }

    public void setUpdatedTimeOnForm(String dateString) {
        try {
            setUpdatedTime(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
