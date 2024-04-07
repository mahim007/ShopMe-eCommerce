package com.mahim.shopme.setting;

import com.mahim.shopme.common.dto.StateDTO;
import com.mahim.shopme.common.entity.Country;
import com.mahim.shopme.common.entity.State;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/settings")
public class StateRestController {

    private final StateRepository stateRepository;

    public StateRestController(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @GetMapping("/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer id) {
        List<State> states = stateRepository.findAllByCountryOrderByNameAsc(new Country(id));
        List<StateDTO> results = new ArrayList<>();

        for (State state : states) {
            results.add(new StateDTO(state.getId(), state.getName()));
        }

        return results;
    }
}
