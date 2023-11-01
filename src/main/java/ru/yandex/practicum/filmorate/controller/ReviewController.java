package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewController {
    ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@RequestBody @Valid Review review) {
        log.info("POST {}, body={}", "\"/reviews\"", review);
        Review reviewToReturn = reviewService.create(review);
        log.debug(reviewToReturn.toString());
        return reviewToReturn;
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        log.info("PUT {}, body={}", "\"/reviews\"", review);
        Review reviewToReturn = reviewService.update(review);
        log.debug(reviewToReturn.toString());
        return reviewToReturn;
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") int id) {
        log.info("GET \"/reviews/" + id + "\"");
        Review reviewToReturn = reviewService.getById(id);
        log.debug(reviewToReturn.toString());
        return reviewToReturn;
    }

    @GetMapping
    public List<Review> getByFilmIdOrGetAll(@RequestParam(required = false) Integer filmId,
                                            @RequestParam(defaultValue = "10") @Min(value = 1) int count) {
        log.info("GET {}, query parameters={}, {}", "\"/reviews\"", "{id=" + filmId + "}", "{count=" + count + "}");

        List<Review> reviewsToReturn = reviewService.getByFilmIdOrGetAll(filmId, count);
        log.debug(reviewsToReturn.toString());
        return reviewsToReturn;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("DELETE \"/reviews/" + id + "\"");
        reviewService.deleteById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id,
                        @PathVariable int userId) {
        log.info("PUT \"/reviews/" + id + "/like/" + userId + "\"");
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id,
                           @PathVariable int userId) {
        log.info("PUT \"/reviews/" + id + "/like/" + userId + "\"");
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping(value = {"/{id}/like/{userId}", "/{id}/dislike/{userId}"})
    public void deleteLikeOrDislike(@PathVariable int id,
                                    @PathVariable int userId) {
        log.info("DELETE \"/reviews/" + id + "/[like, dislike]/" + userId + "\"");
        reviewService.deleteLikeOrDislike(id, userId);
    }
}
