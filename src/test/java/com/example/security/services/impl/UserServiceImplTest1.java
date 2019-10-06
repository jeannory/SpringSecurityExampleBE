package com.example.security.services.impl;

import com.example.security.entities.Role;
import com.example.security.entities.Space;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.SpaceRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import com.example.security.tools.ITools;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.example.security.contants.Constants.*;

public class UserServiceImplTest1 implements ITools {

    private IUserService userService;
    private RoleRepository roleRepository;
    private IRoleService roleService;
    private UserRepository userRepository;
    private SpaceRepository spaceRepository;

    @Before
    public void setUp() throws Exception {
        this.userService = new UserServiceImpl();
        roleRepository = Mockito.mock(RoleRepository.class);
        roleService = Mockito.mock(RoleServiceImpl.class);
        userRepository = Mockito.mock(UserRepository.class);
        spaceRepository = Mockito.mock(SpaceRepository.class);
        Whitebox.setInternalState(userService, "roleRepository", roleRepository);
        Whitebox.setInternalState(userService, "roleService", roleService);
        Whitebox.setInternalState(userService, "userRepository", userRepository);
        Whitebox.setInternalState(userService, "spaceRepository", spaceRepository);
    }

    @Test
    public void test_getDataTest_when_works_fine(){

        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        final Role managerRole = Mockito.spy(new Role());
        managerRole.setId(2L);
        managerRole.setName(MANAGER);
        final Role adminRole = Mockito.spy(new Role());
        adminRole.setId(3L);
        adminRole.setName(ADMIN);

        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(userRole);

        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));
        Set<Role> managerRoles = new HashSet<>(Arrays.asList(userRole, managerRole));
        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, managerRole, adminRole));

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(adminRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        final User user2 = Mockito.spy(new User());
        user2.setId(2L);
        user2.setEmail("jeanne@jeanne.com");
        user2.setPassword(getStringSha3("0000"));
        user2.setRoles(managerRoles);

        final User user3 = Mockito.spy(new User());
        user3.setId(3L);
        user3.setEmail("john@john.com");
        user3.setPassword(getStringSha3("0000"));
        user3.setRoles(userRoles);

        final User user4 = Mockito.spy(new User());
        user4.setId(4L);
        user4.setEmail("johny@johny.com");
        user4.setPassword(getStringSha3("0000"));
        user4.setRoles(userRoles);
        user1.setStatus(Status.ACTIVE);
        user2.setStatus(Status.ACTIVE);
        user3.setStatus(Status.ACTIVE);
        user4.setStatus(Status.INACTIVE);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space1 = Mockito.spy(new Space());
        space1.setId(1L);
        space1.setName("Jean@jean.com space");
        space1.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space1);

        final Space space2 = Mockito.spy(new Space());
        space2.setId(2L);
        space2.setName("jeanne@jeanne.com space");
        space2.setUser(user2);

        final Space space3 = Mockito.spy(new Space());
        space3.setId(3L);
        space3.setName("john@john.com space");
        space3.setUser(user3);

        final Space space4 = Mockito.spy(new Space());
        space4.setId(4L);
        space4.setName("johny@johny.com space");
        space4.setUser(user4);

        //when
        boolean result = userService.getDataTest();

        //then
        Assert.assertTrue(result);
    }

    @Test
    public void test_getDataTest_when_save_role_failed(){

        //given
        final Role userRole = Mockito.spy(new Role());
//        userRole.setId(1L);
        userRole.setName(USER);
        final Role managerRole = Mockito.spy(new Role());
        managerRole.setId(2L);
        managerRole.setName(MANAGER);
        final Role adminRole = Mockito.spy(new Role());
        adminRole.setId(3L);
        adminRole.setName(ADMIN);

        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(userRole);

        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));
        Set<Role> managerRoles = new HashSet<>(Arrays.asList(userRole, managerRole));
        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, managerRole, adminRole));

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(adminRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        final User user2 = Mockito.spy(new User());
        user2.setId(2L);
        user2.setEmail("jeanne@jeanne.com");
        user2.setPassword(getStringSha3("0000"));
        user2.setRoles(managerRoles);

        final User user3 = Mockito.spy(new User());
        user3.setId(3L);
        user3.setEmail("john@john.com");
        user3.setPassword(getStringSha3("0000"));
        user3.setRoles(userRoles);

        final User user4 = Mockito.spy(new User());
        user4.setId(4L);
        user4.setEmail("johny@johny.com");
        user4.setPassword(getStringSha3("0000"));
        user4.setRoles(userRoles);
        user1.setStatus(Status.ACTIVE);
        user2.setStatus(Status.ACTIVE);
        user3.setStatus(Status.ACTIVE);
        user4.setStatus(Status.INACTIVE);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space1 = Mockito.spy(new Space());
        space1.setId(1L);
        space1.setName("Jean@jean.com space");
        space1.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space1);

        final Space space2 = Mockito.spy(new Space());
        space2.setId(2L);
        space2.setName("jeanne@jeanne.com space");
        space2.setUser(user2);

        final Space space3 = Mockito.spy(new Space());
        space3.setId(3L);
        space3.setName("john@john.com space");
        space3.setUser(user3);

        final Space space4 = Mockito.spy(new Space());
        space4.setId(4L);
        space4.setName("johny@johny.com space");
        space4.setUser(user4);

        //when
        boolean result = userService.getDataTest();

        //then
        Assert.assertFalse(result);
    }

    @Test
    public void test_getDataTest_when_save_user_failed(){

        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        final Role managerRole = Mockito.spy(new Role());
        managerRole.setId(2L);
        managerRole.setName(MANAGER);
        final Role adminRole = Mockito.spy(new Role());
        adminRole.setId(3L);
        adminRole.setName(ADMIN);

        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(userRole);

        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));
        Set<Role> managerRoles = new HashSet<>(Arrays.asList(userRole, managerRole));
        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, managerRole, adminRole));

        final User user1 = Mockito.spy(new User());
//        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(adminRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        final User user2 = Mockito.spy(new User());
        user2.setId(2L);
        user2.setEmail("jeanne@jeanne.com");
        user2.setPassword(getStringSha3("0000"));
        user2.setRoles(managerRoles);

        final User user3 = Mockito.spy(new User());
        user3.setId(3L);
        user3.setEmail("john@john.com");
        user3.setPassword(getStringSha3("0000"));
        user3.setRoles(userRoles);

        final User user4 = Mockito.spy(new User());
        user4.setId(4L);
        user4.setEmail("johny@johny.com");
        user4.setPassword(getStringSha3("0000"));
        user4.setRoles(userRoles);
        user1.setStatus(Status.ACTIVE);
        user2.setStatus(Status.ACTIVE);
        user3.setStatus(Status.ACTIVE);
        user4.setStatus(Status.INACTIVE);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space1 = Mockito.spy(new Space());
        space1.setId(1L);
        space1.setName("Jean@jean.com space");
        space1.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space1);

        final Space space2 = Mockito.spy(new Space());
        space2.setId(2L);
        space2.setName("jeanne@jeanne.com space");
        space2.setUser(user2);

        final Space space3 = Mockito.spy(new Space());
        space3.setId(3L);
        space3.setName("john@john.com space");
        space3.setUser(user3);

        final Space space4 = Mockito.spy(new Space());
        space4.setId(4L);
        space4.setName("johny@johny.com space");
        space4.setUser(user4);

        //when
        boolean result = userService.getDataTest();

        //then
        Assert.assertFalse(result);
    }

    @Test
    public void test_getDataTest_when_save_space_failed(){

        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        final Role managerRole = Mockito.spy(new Role());
        managerRole.setId(2L);
        managerRole.setName(MANAGER);
        final Role adminRole = Mockito.spy(new Role());
        adminRole.setId(3L);
        adminRole.setName(ADMIN);

        Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(userRole);

        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));
        Set<Role> managerRoles = new HashSet<>(Arrays.asList(userRole, managerRole));
        Set<Role> adminRoles = new HashSet<>(Arrays.asList(userRole, managerRole, adminRole));

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(adminRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        final User user2 = Mockito.spy(new User());
        user2.setId(2L);
        user2.setEmail("jeanne@jeanne.com");
        user2.setPassword(getStringSha3("0000"));
        user2.setRoles(managerRoles);

        final User user3 = Mockito.spy(new User());
        user3.setId(3L);
        user3.setEmail("john@john.com");
        user3.setPassword(getStringSha3("0000"));
        user3.setRoles(userRoles);

        final User user4 = Mockito.spy(new User());
        user4.setId(4L);
        user4.setEmail("johny@johny.com");
        user4.setPassword(getStringSha3("0000"));
        user4.setRoles(userRoles);
        user1.setStatus(Status.ACTIVE);
        user2.setStatus(Status.ACTIVE);
        user3.setStatus(Status.ACTIVE);
        user4.setStatus(Status.INACTIVE);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space1 = Mockito.spy(new Space());
//        space1.setId(1L);
        space1.setName("Jean@jean.com space");
        space1.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space1);

        final Space space2 = Mockito.spy(new Space());
        space2.setId(2L);
        space2.setName("jeanne@jeanne.com space");
        space2.setUser(user2);

        final Space space3 = Mockito.spy(new Space());
        space3.setId(3L);
        space3.setName("john@john.com space");
        space3.setUser(user3);

        final Space space4 = Mockito.spy(new Space());
        space4.setId(4L);
        space4.setName("johny@johny.com space");
        space4.setUser(user4);

        //when
        boolean result = userService.getDataTest();

        //then
        Assert.assertFalse(result);
    }
}
