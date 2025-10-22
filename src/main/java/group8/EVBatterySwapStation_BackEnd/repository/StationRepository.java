package group8.EVBatterySwapStation_BackEnd.repository;

import group8.EVBatterySwapStation_BackEnd.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByNameContainingIgnoreCase(String name);

    List<Station> findByAddressContainingIgnoreCase(String address);

    List<Station> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

    boolean existsByName(String name);
}
