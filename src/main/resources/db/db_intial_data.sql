
USE home_workout_with_ai;
INSERT INTO muscle (muscle_id, muscle_name) VALUES
(NULL, "Chest"),
(NULL, "Biceps"),
(NULL, "Triceps"),
(NULL, "Back&Lats"),
(NULL, "Leg"),
(NULL, "Abs"),
(NULL, "Shoulders"),
(NULL, "Cardio");
USE home_workout_with_ai;
INSERT INTO exercise (exercise_id, name, level_enum, basic_reps, image_public_id, image_url) VALUES
-- Chest
(NULL, "Knee push-ups", "BEGINNER", 10, NULL, NULL),
(NULL, "Incline push-ups", "BEGINNER", 10, NULL, NULL),-- Triceps
(NULL, "Hold-ease-down push-ups", "BEGINNER", 15, NULL, NULL),
(NULL, "Push-ups", "INTERMEDIATE", 10, NULL, NULL),-- Triceps
(NULL, "Decline push-ups", "INTERMEDIATE", 10, NULL, NULL),
(NULL, "Wide-hands push-ups", "INTERMEDIATE", 10, NULL, NULL),
(NULL, "Hold-ease-up push-ups", "INTERMEDIATE", 15, NULL, NULL),-- Triceps
(NULL, "Archer push-ups (each side)", "ADVANCE", 10, NULL, NULL),
(NULL, "Diamond push-ups", "ADVANCE", 10, NULL, NULL),-- Triceps
(NULL, "Dips", "INTERMEDIATE", 10, NULL, NULL),-- Triceps
(NULL, "Hold-ease-up diamond push-ups", "ADVANCE", 15, NULL, NULL),-- Triceps
-- Biceps
(NULL, "Hold-ease-down pull-ups", "BEGINNER", 15, NULL, NULL),-- Back_Lats
(NULL, "Hold-ease-down chin-ups", "BEGINNER", 15, NULL, NULL),
(NULL, "Horizontal pull-ups", "BEGINNER", 10, NULL, NULL),-- Back_Lats
(NULL, "Half pull-ups with chair", "BEGINNER", 4, NULL, NULL),-- Back_Lats
(NULL, "Half chin-ups with chair", "BEGINNER", 4, NULL, NULL),
(NULL, "Pull-ups", "INTERMEDIATE", 5, NULL, NULL),-- Back_Lats
(NULL, "Chin-ups", "INTERMEDIATE", 5, NULL, NULL),
(NULL, "Holding pull-ups", "INTERMEDIATE", 7, NULL, NULL),-- Back_Lats
(NULL, "Holding chin-ups", "INTERMEDIATE", 7, NULL, NULL),
(NULL, "Archer pull-ups (each side)", "ADVANCE", 5, NULL, NULL),
(NULL, "Pull-ups", "ADVANCE", 8, NULL, NULL),-- Back_Lats
(NULL, "Chin-ups", "ADVANCE", 8, NULL, NULL),
(NULL, "Holding pull-ups", "ADVANCE", 15, NULL, NULL),-- Back_Lats
-- Triceps
(NULL, "Tricep dips", "BEGINNER", 20, NULL, NULL),
(NULL, "Tricep bodyweight extension", "BEGINNER", 10, NULL, NULL),
(NULL, "Side-Lying triceps press (each side)", "INTERMEDIATE", 15, NULL, NULL),
(NULL, "Sphinx push-ups", "ADVANCE", 10, NULL, NULL),
-- Back & Lats
(NULL, "Hold-up horizontal pull-ups", "BEGINNER", 10, NULL, NULL),
(NULL, "Lat pullover", "BEGINNER", 15, NULL, NULL),
(NULL, "Lying towel lat pull down", "INTERMEDIATE", 15, NULL, NULL),
(NULL, "Wide-grid pull-ups", "ADVANCE", 8, NULL, NULL),
-- Leg
(NULL, "Split Squat (each side)", "BEGINNER", 15, NULL, NULL),
(NULL, "Squat", "BEGINNER", 15, NULL, NULL),
(NULL, "Back rack split squat (each side)", "BEGINNER", 15, NULL, NULL),
(NULL, "Highknee run (each side)", "BEGINNER", 20, NULL, NULL),-- Cardio
(NULL, "Lateral Lunge (each side)", "INTERMEDIATE", 15, NULL, NULL),
(NULL, "Highknee run (each side)", "INTERMEDIATE", 30, NULL, NULL),-- Cardio
(NULL, "Squat", "INTERMEDIATE", 20, NULL, NULL),
(NULL, "Stepup (each side)", "ADVANCE", 15, NULL, NULL),
(NULL, "Jump squat", "ADVANCE", 20, NULL, NULL),
(NULL, "Skaters (each side)", "ADVANCE", 20, NULL, NULL),-- Cardio
(NULL, "Single-leg squat", "ADVANCE", 20, NULL, NULL),
-- Abs
(NULL, "Hold plank", "BEGINNER", 20, NULL, NULL),-- Cardio
(NULL, "Mountain climbers", "BEGINNER", 20, NULL, NULL),-- Cardio
(NULL, "Knee touch crunch", "BEGINNER", 20, NULL, NULL),-- Cardio
(NULL, "Hold side plank (each side)", "BEGINNER", 10, NULL, NULL),
(NULL, "Leg lift", "BEGINNER", 20, NULL, NULL),
(NULL, "Crunch", "INTERMEDIATE", 20, NULL, NULL),-- Cardio
(NULL, "Leg raise hip lift", "INTERMEDIATE", 20, NULL, NULL),-- Cardio
(NULL, "Hold side plank (each side)", "INTERMEDIATE", 15, NULL, NULL),-- Shoulders
(NULL, "Oblique crunch (each side)", "INTERMEDIATE", 20, NULL, NULL),
(NULL, "Leg pull-in knee-ups", "INTERMEDIATE", 20, NULL, NULL),-- Cardio
(NULL, "Elbow-to-knee crunch (each side)", "ADVANCE", 20, NULL, NULL),-- Cardio
(NULL, "Leg raise hip lift", "ADVANCE", 30, NULL, NULL),-- Cardio
(NULL, "Crunch", "ADVANCE", 30, NULL, NULL),-- Cardio
(NULL, "Lying alternating leg raise (each side)", "ADVANCE", 20, NULL, NULL),
(NULL, "Hold side plank (each side)", "ADVANCE", 25, NULL, NULL),
-- Shoulders
(NULL, "Pike push-ups", "BEGINNER", 15, NULL, NULL),
(NULL, "Pseudo push-ups", "INTERMEDIATE", 10, NULL, NULL),
(NULL, "Hold side plank (each side)", "INTERMEDIATE", 20, NULL, NULL),
(NULL, "Pseudo push-ups", "INTERMEDIATE", 15, NULL, NULL),
(NULL, "Hold tucked planche", "INTERMEDIATE", 10, NULL, NULL),
-- Cardio
(NULL, "Hand walk-out", "BEGINNER", 10, NULL, NULL),
(NULL, "Push squat repeat", "INTERMEDIATE", 15, NULL, NULL),
(NULL, "Hold plank", "INTERMEDIATE", 30, NULL, NULL),
(NULL, "Jump-squat to push-ups", "ADVANCE", 15, NULL, NULL);
-- ---------------------------------------------------------------
INSERT INTO muscle_exercise (id, muscle_id, exercise_id) VALUES
(NULL, 1, 1), -- Knee push-ups
(NULL, 1, 2), -- Incline push-ups
(NULL, 3, 2), -- Incline push-ups - TRICEPS
(NULL, 1, 3), -- Hold-ease-down push-ups
(NULL, 1, 4), -- Push-ups
(NULL, 3, 4), -- Push-ups - TRICEPS
(NULL, 1, 5), -- Decline push-ups
(NULL, 1, 6), -- Wide-hands push-ups
(NULL, 1, 7), -- Hold-ease-up push-ups
(NULL, 3, 7), -- Hold-ease-up push-ups - TRICEPS
(NULL, 1, 8), -- Archer push-ups (each side)
(NULL, 1, 9), -- Diamond push-ups
(NULL, 3, 9), -- Diamond push-ups - TRICEPS
(NULL, 1, 10), -- Dips
(NULL, 3, 10), -- Dips - TRICEPS
(NULL, 1, 11), -- Hold-ease-up diamond push-ups
(NULL, 3, 11), -- Hold-ease-up diamond push-ups - TRICEPS
-- Biceps
(NULL, 2, 12), -- Hold-ease-down pull-ups
(NULL, 4, 12), -- Hold-ease-down pull-ups - Back_Lats
(NULL, 2, 13), -- Hold-ease-down chin-ups
(NULL, 2, 14), -- Horizontal pull-ups
(NULL, 4, 14), -- Horizontal pull-ups - Back_Lats
(NULL, 2, 15), -- Half pull-ups with chair
(NULL, 4, 15), -- Half pull-ups with chair - Back_Lats
(NULL, 2, 16), -- Half chin-ups with chair
(NULL, 2, 17), -- Pull-ups
(NULL, 4, 17), -- Pull-ups - Back_Lats
(NULL, 2, 18), -- Chin-ups
(NULL, 2, 19), -- Holding pull-ups
(NULL, 4, 19), -- Holding pull-ups - Back_Lats
(NULL, 2, 20), -- Holding chin-ups
(NULL, 2, 21), -- Archer pull-ups (each side)
(NULL, 4, 21), -- Archer pull-ups (each side) - Back_Lats
(NULL, 2, 22), -- Pull-ups
(NULL, 2, 23), -- Chin-ups
(NULL, 4, 23), -- Chin-ups - Back_Lats
(NULL, 2, 24), -- Holding pull-ups
(NULL, 4, 24), -- Holding pull-ups - Back_Lats
-- Triceps
(NULL, 3, 25), -- Tricep dips
(NULL, 3, 26), -- Tricep bodyweight extension
(NULL, 3, 27), -- Side-Lying triceps press (each side)
(NULL, 3, 28), -- Sphinx push-ups
(NULL, 1, 28), -- Sphinx push-ups
-- Back & Lats
(NULL, 4, 29), -- Hold-up horizontal pull-ups
(NULL, 4, 30), -- Lat pullover
(NULL, 4, 31), -- Lying towel lat pull down
(NULL, 4, 32), -- Wide-grid pull-ups
-- Leg
(NULL, 5, 33), -- Split Squat (each side)
(NULL, 5, 34), -- Squat
(NULL, 5, 35), -- Back rack split squat (each side)
(NULL, 5, 36), -- Highknee run (each side)
(NULL, 7, 36), -- Highknee run (each side) - Cardio
(NULL, 5, 37), -- Lateral Lunge (each side)
(NULL, 5, 38), -- Highknee run (each side)
(NULL, 7, 38), -- Highknee run (each side) - Cardio
(NULL, 5, 39), -- Squat
(NULL, 5, 40), -- Stepup (each side)
(NULL, 5, 41), -- Jump squat
(NULL, 5, 42), -- Skaters (each side)
(NULL, 7, 42), -- Skaters (each side) - Cardio
(NULL, 5, 43), -- Single-leg squat
-- Abs
(NULL, 6, 44), -- Hold plank
(NULL, 7, 44), -- Hold plank - Cardio
(NULL, 6, 45), -- Mountain climbers
(NULL, 7, 45), -- Mountain climbers - Cardio
(NULL, 6, 46), -- Knee touch crunch
(NULL, 7, 46), -- Knee touch crunch - Cardio
(NULL, 6, 47), -- Hold side plank (each side)
(NULL, 6, 48), -- Leg lift
(NULL, 6, 49), -- Crunch
(NULL, 7, 49), -- Crunch - Cardio
(NULL, 6, 50), -- Leg raise hip lift
(NULL, 7, 50), -- Leg raise hip lift - Cardio
(NULL, 6, 51), -- Hold side plank (each side)
(NULL, 7, 51), -- Hold side plank (each side) - Shoulders
(NULL, 6, 52), -- Oblique crunch (each side)
(NULL, 6, 53), -- Leg pull-in knee-ups
(NULL, 7, 53), -- Leg pull-in knee-ups - Cardio
(NULL, 6, 54), -- Elbow-to-knee crunch (each side)
(NULL, 7, 54), -- Elbow-to-knee crunch (each side) - Cardio
(NULL, 6, 55), -- Leg raise hip lift
(NULL, 7, 55), -- Leg raise hip lift - Cardio
(NULL, 6, 56), -- Crunch
(NULL, 7, 56), -- Crunch - Cardio
(NULL, 6, 57), -- Lying alternating leg raise (each side)
(NULL, 6, 58), -- Hold side plank (each side)
-- Shoulders
(NULL, 7, 59), -- Pike push-ups
(NULL, 7, 60), -- Pseudo push-ups
(NULL, 7, 61), -- Hold side plank (each side)
(NULL, 7, 62), -- Pseudo push-ups
(NULL, 7, 63), -- Hold tucked planche
-- Cardio
(NULL, 8, 64), -- Hand walk-out
(NULL, 8, 65), -- Push squat repeat
(NULL, 8, 66), -- Hold plank
(NULL, 8, 67); -- Jump-squat to push-ups

USE home_workout_with_ai;
INSERT INTO session (session_id, name, level_enum, description, switch_exercise_delay) VALUES
(NULL, "Session: Chest - Triceps - Shoulders - Abs", "BEGINNER", "Thick - Normal Body want to Gain - Maintain Weight", 300),
(NULL, "Session: Biceps - Back - Lats - Leg", "BEGINNER", "Thick - Normal Body want to Gain - Maintain Weight", 300);
INSERT INTO muscle_session (session_id, muscle_id) VALUES
(1, 1),  -- CHEST
(1, 3),  -- TRICEPS
(1, 7),  -- SHOULDERS
(1, 6),  -- ABS
(2, 2),  -- BICEPS
(2, 4),  -- BACK_LATS
(2, 5);  -- LEG
INSERT INTO exercises_of_sessions (session_id, exercise_id, ordinal, down_reps_ratio, slack_in_second, raise_slack_in_second, iteration, need_switch_exercise_delay) VALUES
-- CHEST, TRICEPS
(1, 1, 1, 0.2, 45, 15, 3, 1),
(1, 2, 2, 0.2, 45, 15, 3, 1),
(1, 3, 3, 0.2, 45, 15, 3, 1),
-- SHOULDERS
(1, 59, 4, 0.2, 45, 15, 3, 1),
-- ABS
(1, 48, 5, 0, 30, 0, 2, 0),
(1, 45, 6, 0, 30, 0, 2, 0),
(1, 46, 7, 0, 30, 0, 2, 0),
(1, 44, 8, 0, 30, 0, 1, 0),
(1, 46, 9, 0, 30, 0, 1, 0),
-- BICEPS & BACK_LATS
(2, 15, 1, 0.2, 45, 30, 3, 1),
(2, 16, 2, 0.2, 45, 30, 3, 1),
(2, 12, 3, 0.2, 45, 30, 3, 1),
(2, 13, 4, 0.2, 45, 30, 3, 1),
(2, 30, 5, 0.2, 45, 30, 3, 1),
-- LEG
(2, 33, 6, 0.2, 30, 0, 2, 0),
(2, 34, 7, 0.2, 30, 0, 2, 0),
(2, 35, 8, 0.2, 45, 0, 2, 0),
(2, 36, 9, 0.2, 45, 0, 2, 0);
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Gain - Maintain Weight for Thick or Normal Body with 2 Sessions", 0, "BEGINNER", "Full body without Cardio");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (1, 1, 1), (1, 2, 2);
-- -----------------------------------------------------------
INSERT INTO session (session_id, name, level_enum, description, switch_exercise_delay) VALUES
(NULL, "Session: Chest - Triceps - Shoulders", "BEGINNER", "Thick - Normal Body want to Gain - Maintain Weight", 300),
(NULL, "Session: Abs", "BEGINNER", "Thick - Normal Body want to Gain - Maintain Weight", 300);
INSERT INTO muscle_session (session_id, muscle_id) VALUES
(3, 1),  -- CHEST
(3, 3),  -- TRICEPS
(3, 7),  -- SHOULDERS
(4, 6);  -- ABS
INSERT INTO exercises_of_sessions (session_id, exercise_id, ordinal, down_reps_ratio, slack_in_second, raise_slack_in_second, iteration, need_switch_exercise_delay) VALUES
-- CHEST, TRICEPS
(3, 1, 1, 0.2, 45, 15, 3, 1),
(3, 2, 2, 0.2, 45, 15, 3, 1),
(3, 3, 3, 0.2, 45, 15, 3, 1),
(3, 25, 4, 0.2, 45, 15, 3, 1),
-- SHOULDERS
(3, 59, 5, 0.2, 45, 15, 3, 1),
-- ABS
(4, 48, 1, 0, 30, 0, 3, 0),
(4, 45, 2, 0, 30, 0, 2, 0),
(4, 46, 3, 0, 30, 0, 2, 0),
(4, 44, 4, 0, 30, 0, 2, 0),
(4, 46, 5, 0, 30, 0, 2, 0);
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Gain - Maintain Weight for Thick or Normal Body with 3 Sessions", 0, "BEGINNER", "Full body without Cardio");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (2, 3, 1), (2, 4, 2), (2, 2, 3);
-- -----------------------------------------------------------
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Gain - Maintain Weight for Thick or Normal Body with 4 Sessions", 0, "BEGINNER", "Full body without Cardio");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (3, 1, 1), (3, 2, 2), (3, 1, 3), (3, 2, 4);
-- -----------------------------------------------------------
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Gain - Maintain Weight for Thick or Normal Body with 5 Sessions", 0, "BEGINNER", "Full body without Cardio");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (4, 3, 1), (4, 2, 2), (4, 4, 3), (4, 3, 4), (4, 2, 5);
-- -----------------------------------------------------------
INSERT INTO session (session_id, name, level_enum, description, switch_exercise_delay) VALUES
(NULL, "Session: Cardio", "BEGINNER", "Thick - Normal Body want to Gain - Maintain Weight", 300);
INSERT INTO home_workout_with_ai.muscle_session (session_id, muscle_id) VALUES
(5, 8);  -- CARDIO
INSERT INTO exercises_of_sessions (session_id, exercise_id, ordinal, down_reps_ratio, slack_in_second, raise_slack_in_second, iteration, need_switch_exercise_delay) VALUES
-- CARDIO
(5, 45, 1, 0, 30, 0, 2, 0),
(5, 46, 2, 0, 30, 0, 2, 0),
(5, 64, 3, 0, 30, 0, 2, 0),
(5, 44, 4, 0, 45, 0, 2, 0),
(5, 36, 5, 0, 45, 0, 2, 0);
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Gain - Maintain Weight for Thick or Normal Body with 6 Sessions", 0, "BEGINNER", "Full body without Cardio");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (5, 3, 1), (5, 2, 2), (5, 4, 3), (5, 5, 4), (5, 3, 5), (5, 2, 6);


USE home_workout_with_ai;
INSERT INTO session (session_id, name, level_enum, description, switch_exercise_delay) VALUES
(NULL, "Session: Chest - Triceps - Shoulders - Cardio", "BEGINNER", "Fat Body want to Lose Weight", 300);
INSERT INTO muscle_session (session_id, muscle_id) VALUES
(6, 1),  -- CHEST
(6, 3),  -- TRICEPS
(6, 7),  -- SHOULDERS
(6, 8);  -- CARDIO
INSERT INTO exercises_of_sessions (session_id, exercise_id, ordinal, down_reps_ratio, slack_in_second, raise_slack_in_second, iteration, need_switch_exercise_delay) VALUES
-- CHEST, TRICEPS
(6, 1, 1, 0.2, 45, 15, 3, 1),
(6, 2, 2, 0.2, 45, 15, 3, 1),
(6, 3, 3, 0.2, 45, 15, 3, 1),
-- SHOULDERS
(6, 59, 4, 0.2, 45, 15, 3, 1),
-- CARDIO
(6, 45, 5, 0, 30, 0, 2, 0),
(6, 46, 6, 0, 30, 0, 2, 0),
(6, 64, 7, 0, 30, 0, 2, 0),
(6, 44, 8, 0, 30, 0, 1, 0),
(6, 36, 9, 0, 30, 0, 1, 0);
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Lose Weight for Fat Body with 2 Sessions", 0, "BEGINNER", "Full body");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (6, 6, 1), (6, 2, 2);
-- -----------------------------------------------------------
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Lose Weight for Fat Body with 3 Sessions", 0, "BEGINNER", "Full body");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES (7, 1, 1), (7, 5, 2), (7, 2, 3);
-- -----------------------------------------------------------
INSERT INTO session (session_id, name, level_enum, description, switch_exercise_delay) VALUES
(NULL, "Session: Chest - Triceps - Biceps - Back - Lats - Shoulders", "BEGINNER", "Fat Body want to Lose Weight", 300),
(NULL, "Session: Abs - Leg", "BEGINNER", "Fat Body want to Lose Weight", 300);
INSERT INTO muscle_session (session_id, muscle_id) VALUES
(7, 1),  -- CHEST
(7, 3),  -- TRICEPS
(7, 2),  -- BICEPS
(7, 4),  -- BACK_LATS
(7, 7),  -- SHOULDERS
(8, 6),  -- ABS
(8, 5);  -- LEG
INSERT INTO exercises_of_sessions (session_id, exercise_id, ordinal, down_reps_ratio, slack_in_second, raise_slack_in_second, iteration, need_switch_exercise_delay) VALUES
-- CHEST, TRICEPS
(7, 1, 1, 0.2, 45, 15, 3, 1),
(7, 2, 2, 0.2, 45, 15, 3, 1),
(7, 3, 3, 0.2, 45, 15, 3, 1),
-- SHOULDERS
(7, 59, 4, 0.2, 45, 15, 3, 1),
-- BICEPS & BACK_LATS
(7, 15, 5, 0.2, 45, 30, 3, 1),
(7, 16, 6, 0.2, 45, 30, 3, 1),
(7, 12, 7, 0.2, 45, 30, 3, 1),
(7, 13, 8, 0.2, 45, 30, 3, 1),
(7, 30, 9, 0.2, 45, 30, 3, 1),
-- ABS
(8, 48, 1, 0, 30, 0, 3, 0),
(8, 45, 2, 0, 30, 0, 2, 0),
(8, 46, 3, 0, 30, 0, 2, 0),
(8, 44, 4, 0, 30, 0, 2, 0),
(8, 46, 5, 0, 30, 0, 2, 0),
-- LEG
(8, 33, 6, 0.2, 30, 0, 2, 0),
(8, 34, 7, 0.2, 30, 0, 2, 0),
(8, 35, 8, 0.2, 45, 0, 2, 0),
(8, 36, 9, 0.2, 45, 0, 2, 0);
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Lose Weight for Fat Body with 4 Sessions", 0, "BEGINNER", "Full body");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES
(8, 7, 1), (8, 5, 2), (8, 7, 3), (8, 8, 4);
-- -----------------------------------------------------------
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Lose Weight for Fat Body with 5 Sessions", 0, "BEGINNER", "Full body");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES
(9, 1, 1), (9, 2, 2), (9, 5, 3), (9, 1, 4), (9, 2, 5);
-- -----------------------------------------------------------
INSERT INTO schedule (schedule_id, name, coins, level_enum, description) VALUES
(NULL, "Lose Weight for Fat Body with 6 Sessions", 0, "BEGINNER", "Full body");
INSERT INTO sessions_of_schedules (schedule_id, session_id, ordinal) VALUES
(10, 3, 1), (10, 2, 2), (10, 4, 3), (10, 5, 4), (10, 3, 5), (10, 2, 6);

INSERT INTO home_workout_with_ai.subscription
(subscription_id, aim, bmr, subscribed_time, efficient_days, is_efficient, rep_ratio, weight_aim, completed_time, schedule_id, user_info_id)
VALUES
    (NULL, "MAINTAIN_WEIGHT", 1580.0, "2024-01-01 09:12:45", NULL, NULL, 100, 70, "2025-01-01 01:00:00", 1, 1),
    (NULL, "WEIGHT_UP", 1580.0, "2024-01-01 09:00:00", NULL, NULL, 100, 70, "2025-01-01 01:00:00", 2, 1),
    (NULL, "WEIGHT_DOWN", 1580.0, "2024-01-01 09:00:45", 158, NULL, 100, 65, NULL, 3, 1);