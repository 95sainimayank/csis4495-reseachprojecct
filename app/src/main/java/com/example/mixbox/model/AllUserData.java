package com.example.mixbox.model;

import java.util.List;

public class AllUserData {
   List<User> allUsers;

   public AllUserData() {
   }

   public AllUserData(List<User> allUsers) {
      this.allUsers = allUsers;
   }

   public List<User> getAllUsers() {
      return allUsers;
   }

   public void setAllUsers(List<User> allUsers) {
      this.allUsers = allUsers;
   }
}
