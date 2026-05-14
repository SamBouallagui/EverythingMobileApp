package com.example.everything;

import com.example.everything.models.api.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    // Auth endpoints
    @POST("api/Auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/Auth/register")
    Call<String> register(@Body RegisterRequest request);

    // Communities endpoints
    @GET("api/Communities")
    Call<List<CommunityDto>> getAllCommunities();

    @POST("api/Communities")
    Call<CommunityDto> createCommunity(@Body CreateCommunityRequest request);

    @GET("api/Communities/{id}")
    Call<CommunityDto> getCommunity(@Path("id") int id);

    @PATCH("api/Communities/{id}")
    Call<CommunityDto> updateCommunity(@Path("id") int id, @Body CreateCommunityRequest request);

    @DELETE("api/Communities/{id}")
    Call<String> deleteCommunity(@Path("id") int id);

    @GET("api/Communities/joined")
    Call<List<CommunityDto>> getJoinedCommunities();

    @POST("api/Communities/{id}/join")
    Call<String> joinCommunity(@Path("id") int id);

    @POST("api/Communities/{id}/leave")
    Call<String> leaveCommunity(@Path("id") int id);

    // Events endpoints
    @GET("api/communities/{communityId}/events")
    Call<List<EventDto>> getCommunityEvents(@Path("communityId") int communityId);

    @POST("api/communities/{communityId}/events")
    Call<EventDto> createCommunityEvent(@Path("communityId") int communityId, @Body CreateEventRequest request);

    @GET("api/events/{eventId}")
    Call<EventDto> getEvent(@Path("eventId") int eventId);

    @PATCH("api/events/{eventId}")
    Call<EventDto> updateEvent(@Path("eventId") int eventId, @Body CreateEventRequest request);

    @DELETE("api/events/{eventId}")
    Call<String> deleteEvent(@Path("eventId") int eventId);

    @POST("api/events/{eventId}/participate")
    Call<String> participateInEvent(@Path("eventId") int eventId);

    @GET("api/events/my-events")
    Call<List<EventDto>> getMyEvents();

    @GET("api/events/{eventId}/participants")
    Call<List<UserDto>> getEventParticipants(@Path("eventId") int eventId);

    // Members endpoints
    @GET("api/communities/{communityId}/members")
    Call<List<MemberDto>> getCommunityMembers(@Path("communityId") int communityId);

    @GET("api/communities/{communityId}/members/me")
    Call<MemberDto> getCurrentUserMember(@Path("communityId") int communityId);

    @PUT("api/communities/{communityId}/members/{targetUserId}/role")
    Call<String> updateMemberRole(@Path("communityId") int communityId, @Path("targetUserId") int targetUserId, @Body UpdateMemberRoleRequest request);

    // Posts endpoints
    @GET("api/communities/{communityId}/posts")
    Call<List<PostDto>> getCommunityPosts(@Path("communityId") int communityId);

    @POST("api/communities/{communityId}/posts")
    Call<PostDto> createCommunityPost(@Path("communityId") int communityId, @Body CreatePostRequest request);

    @GET("api/posts/{postId}")
    Call<PostDto> getPost(@Path("postId") int postId);

    @PATCH("api/posts/{postId}")
    Call<PostDto> updatePost(@Path("postId") int postId, @Body CreatePostRequest request);

    @DELETE("api/posts/{postId}")
    Call<String> deletePost(@Path("postId") int postId);

    @POST("api/posts/{postId}/like")
    Call<String> togglePostLike(@Path("postId") int postId);

    @GET("api/posts/{postId}/comments")
    Call<List<CommentDto>> getPostComments(@Path("postId") int postId);

    @POST("api/posts/{postId}/comments")
    Call<CommentDto> createPostComment(@Path("postId") int postId, @Body CreateCommentRequest request);

    @PATCH("api/posts/{postId}/comments/{commentId}")
    Call<CommentDto> updateComment(@Path("postId") int postId, @Path("commentId") int commentId, @Body CreateCommentRequest request);

    @DELETE("api/posts/{postId}/comments/{commentId}")
    Call<String> deleteComment(@Path("postId") int postId, @Path("commentId") int commentId);

    // Users endpoints
    @GET("api/Users")
    Call<List<UserDto>> getAllUsers();

    @GET("api/Users/{id}")
    Call<UserDto> getUser(@Path("id") int id);

    @PUT("api/Users/{id}")
    Call<UserDto> updateUser(@Path("id") int id, @Body UpdateUserRequest request);

    @DELETE("api/Users/{id}")
    Call<String> deleteUser(@Path("id") int id);

    @GET("api/Users/me")
    Call<UserDto> getCurrentUser();
}