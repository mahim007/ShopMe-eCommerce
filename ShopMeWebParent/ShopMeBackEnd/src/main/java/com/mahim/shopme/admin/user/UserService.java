package com.mahim.shopme.admin.user;

import com.mahim.shopme.admin.FileUploadUtil;
import com.mahim.shopme.admin.paging.PagingAndSortingHelper;
import com.mahim.shopme.common.entity.Role;
import com.mahim.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.mahim.shopme.common.util.StaticPathUtils.USER_UPLOAD_DIR;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 10;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return (List<User>) userRepository.findAll();
    }

    public Page<User> listByKeyword(int pageNum, PagingAndSortingHelper helper) {
        return (Page<User>) helper.listEntities(pageNum, USERS_PER_PAGE, userRepository);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = user.getId() != null;

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public User updateAccount(User userInForm) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userInForm.getId());
        if (userOptional.isPresent()) {
            User userInDB = userOptional.get();
            if (!userInForm.getPassword().isEmpty()) {
                userInDB.setPassword(userInForm.getPassword());
                encodePassword(userInDB);
            }

            if (userInForm.getPhotos() != null) {
                userInDB.setPhotos(userInForm.getPhotos());
            }

            userInDB.setFirstName(userInForm.getFirstName());
            userInDB.setLastName(userInForm.getLastName());

            return userRepository.save(userInDB);
        } else {
            throw new UserNotFoundException("No user found with id: " + userInForm.getId());
        }
    }

    public User findUserById(Integer id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("No user found with id: " + id);
        }
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            return user;
        }

        throw new UserNotFoundException("User not found (EMAIL: " + email + ")");
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("No user found with id: " + id);
        }

        userRepository.deleteById(id);
        FileUploadUtil.removeDir(USER_UPLOAD_DIR + "/" + id);
    }

    public void updateEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException {
        Long countById = userRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new UserNotFoundException("User not found (ID: " + id + ")");
        }
        userRepository.updateEnabledStatus(id, !enabled);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);
        if (userByEmail == null) return true;
        boolean isCreatingNewUser = (id == null);

        if (isCreatingNewUser) {
            return false;
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }
}
