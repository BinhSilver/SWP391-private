﻿CREATE DATABASE Wasabii;
GO
USE Wasabii;
GO

-- 1. Roles
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName NVARCHAR(50) UNIQUE NOT NULL
);
INSERT INTO Roles VALUES (1, 'Free'), (2, 'Premium'), (3, 'Teacher'), (4, 'Admin');

-- 2. Premium Plans
CREATE TABLE PremiumPlans (
    PlanID INT PRIMARY KEY IDENTITY,
    PlanName NVARCHAR(100),
    Price DECIMAL(10, 2),
    DurationInMonths INT,
    Description NVARCHAR(255)
);

-- 3. Users
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY,
    RoleID INT FOREIGN KEY REFERENCES Roles(RoleID),
    Email NVARCHAR(255) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255),
    GoogleID NVARCHAR(255),
    FullName NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1,
    IsLocked BIT DEFAULT 0,
    BirthDate DATE,
    PhoneNumber NVARCHAR(20),
    JapaneseLevel NVARCHAR(50),
    Address NVARCHAR(255),
    Country NVARCHAR(100),
    Avatar NVARCHAR(MAX),
    Gender NVARCHAR(10) CONSTRAINT DF_Users_Gender DEFAULT N'Khác'
);

-- 4. Payments
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    PlanID INT NOT NULL,
    Amount DECIMAL(10, 2),
    PaymentDate DATETIME DEFAULT GETDATE(),
    TransactionNo NVARCHAR(100),
    BankCode NVARCHAR(50),
    OrderInfo NVARCHAR(255),
    ResponseCode NVARCHAR(10),
    TransactionStatus NVARCHAR(20),
    OrderCode NVARCHAR(100),
    CheckoutUrl NVARCHAR(255),
    Status NVARCHAR(20),
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Payments_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_Payments_Plans FOREIGN KEY (PlanID) REFERENCES PremiumPlans(PlanID)
);

-- 5. UserPremium
CREATE TABLE UserPremium (
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    PlanID INT FOREIGN KEY REFERENCES PremiumPlans(PlanID),
    StartDate DATETIME,
    EndDate DATETIME,
    PRIMARY KEY (UserID, PlanID)
);

-- 6. Courses & Lessons
CREATE TABLE Courses (
    CourseID INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(MAX),   
    IsHidden BIT DEFAULT 0,
    IsSuggested BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Lessons (
    LessonID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255),
    IsHidden BIT DEFAULT 0
);

-- 7. Enrollment
CREATE TABLE Enrollment (
    EnrollmentID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    EnrolledAt DATETIME DEFAULT GETDATE()
);

-- 8. Course Ratings
CREATE TABLE CourseRatings (
    RatingID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    RatedAt DATETIME DEFAULT GETDATE()
);

-- 9. Lesson Materials
CREATE TABLE LessonMaterials (
    MaterialID INT PRIMARY KEY IDENTITY(1,1),
    LessonID INT NOT NULL,
    MaterialType NVARCHAR(50) NOT NULL,   -- 'Từ vựng' | 'Kanji' | 'Ngữ pháp'
    FileType NVARCHAR(50) NOT NULL,       -- 'PDF' | 'Video'
    Title NVARCHAR(255),
    FilePath NVARCHAR(MAX),
    IsHidden BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),
	);




-- 10. Vocabulary, Tags, VocabularyTags
CREATE TABLE Vocabulary (
    VocabID INT PRIMARY KEY IDENTITY,
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    Reading NVARCHAR(100),
    Example NVARCHAR(MAX)
);

CREATE TABLE Tags (
    TagID INT PRIMARY KEY IDENTITY,
    TagName NVARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE VocabularyTags (
    VocabID INT FOREIGN KEY REFERENCES Vocabulary(VocabID),
    TagID INT FOREIGN KEY REFERENCES Tags(TagID),
    PRIMARY KEY (VocabID, TagID)
);

-- 11. User Vocabulary
CREATE TABLE UserVocabulary (
    UserVocabID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 12. Kanji
CREATE TABLE Kanji (
    KanjiID INT PRIMARY KEY IDENTITY,
    Character NVARCHAR(10),
    Onyomi NVARCHAR(100),
    Kunyomi NVARCHAR(100),
    Meaning NVARCHAR(255),
    StrokeCount INT,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID)
);

CREATE TABLE VocabularyKanji (
    VocabID INT FOREIGN KEY REFERENCES Vocabulary(VocabID),
    KanjiID INT FOREIGN KEY REFERENCES Kanji(KanjiID),
    PRIMARY KEY (VocabID, KanjiID)
);

-- 13. Grammar Points
CREATE TABLE GrammarPoints (
    GrammarID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255),
    Explanation NVARCHAR(MAX)
);

-- 14. Flashcards
CREATE TABLE Flashcards (
    FlashcardID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Title NVARCHAR(100)
);

CREATE TABLE FlashcardItems (
    FlashcardItemID INT PRIMARY KEY IDENTITY,
    FlashcardID INT FOREIGN KEY REFERENCES Flashcards(FlashcardID),
    VocabID INT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    UserVocabID INT NULL FOREIGN KEY REFERENCES UserVocabulary(UserVocabID),
    Note NVARCHAR(255)
);

-- 15. Quiz & Test (Updated)
CREATE TABLE Quizzes (
    QuizID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255)
);

CREATE TABLE Tests (
    TestID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255)
);

CREATE TABLE Questions (
    QuestionID INT PRIMARY KEY IDENTITY,
    QuizID INT NULL FOREIGN KEY REFERENCES Quizzes(QuizID),
    TestID INT NULL FOREIGN KEY REFERENCES Tests(TestID),
    QuestionText NVARCHAR(MAX),
    TimeLimit INT DEFAULT 30
);

CREATE TABLE Answers (
    AnswerID INT PRIMARY KEY IDENTITY,
    QuestionID INT FOREIGN KEY REFERENCES Questions(QuestionID),
    AnswerText NVARCHAR(MAX),
    IsCorrect BIT
);

-- 16. Quiz & Test Results
CREATE TABLE QuizResults (
    ResultID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    QuizID INT FOREIGN KEY REFERENCES Quizzes(QuizID),
    Score INT,
    TakenAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE TestResults (
    ResultID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    TestID INT FOREIGN KEY REFERENCES Tests(TestID),
    Score INT,
    TakenAt DATETIME DEFAULT GETDATE()
);

-- 17. Feedback
CREATE TABLE Feedbacks (
    FeedbackID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Content NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE FeedbackVotes (
    VoteID INT PRIMARY KEY IDENTITY,
    FeedbackID INT FOREIGN KEY REFERENCES Feedbacks(FeedbackID),
    UserID INT FOREIGN KEY REFERENCES Users(UserID)
);

-- 18. Chat & Video Call
CREATE TABLE Chats (
    ChatID INT PRIMARY KEY IDENTITY,
    SenderID INT FOREIGN KEY REFERENCES Users(UserID),
    ReceiverID INT FOREIGN KEY REFERENCES Users(UserID),
    Message NVARCHAR(MAX),
    SentAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE VideoCalls (
    CallID INT PRIMARY KEY IDENTITY,
    CallerID INT FOREIGN KEY REFERENCES Users(UserID),
    ReceiverID INT FOREIGN KEY REFERENCES Users(UserID),
    CallStart DATETIME,
    CallEnd DATETIME
);

-- 19. Progress
CREATE TABLE Progress (
    ProgressID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    CompletionPercent INT CHECK (CompletionPercent BETWEEN 0 AND 100),
    LastAccessed DATETIME DEFAULT GETDATE()
);

-- 20. Rooms & Participants
CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY IDENTITY(1,1),
    HostUserID INT NOT NULL,
    LanguageLevel NVARCHAR(50) NOT NULL,
    GenderPreference NVARCHAR(20) NOT NULL DEFAULT N'Không yêu cầu',
    MinAge INT CHECK (MinAge >= 0),
    MaxAge INT,
    AllowApproval BIT DEFAULT 0,
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Rooms_Host FOREIGN KEY (HostUserID) REFERENCES Users(UserID) ON DELETE CASCADE,
    CONSTRAINT CK_Rooms_GenderPreference CHECK (
        GenderPreference IN (N'Nam', N'Nữ', N'Không yêu cầu')
    )
);

CREATE TABLE RoomParticipants (
    RoomID INT NOT NULL,
    UserID INT NOT NULL,
    JoinedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT PK_RoomParticipants PRIMARY KEY (RoomID, UserID),
    CONSTRAINT FK_RoomParticipants_Room FOREIGN KEY (RoomID) REFERENCES Rooms(RoomID) ON DELETE CASCADE,
    CONSTRAINT FK_RoomParticipants_User FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE NO ACTION
);

-- Seed data
INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description)
VALUES 
    (N'Gói Tháng', 25000, 1, N'Sử dụng Premium trong 1 tháng'),
    (N'Gói Năm', 250000, 12, N'Sử dụng Premium trong 12 tháng');




INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender)
VALUES (1, 'nguyenphamthanhbinh02@gmail.com', '123456789', NULL, N'Người Dùng', '2000-01-01', '0123456789', N'N5', N'Địa chỉ user', N'Việt Nam', NULL, N'Nam'),
       (4, 'admin@gmail.com', '123456789', NULL, N'Quản Trị Viên', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nữ');

--thêm user này vô để coi được khóa học nha
INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender)
VALUES (3,'teacher@gmail.com', '123', NULL, 'Tanaka Sensei', '2004-07-04', '0911053612', 'N4', N'Địa chỉ teacher', N'Việt Nam', NULL, N'Nữ');


--Thêm mấy bảng này nha
CREATE TABLE LessonAccess (
    AccessID INT IDENTITY PRIMARY KEY,
    UserID INT NOT NULL,
    LessonID INT NOT NULL,
    AccessedAt DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_LessonAccess_User FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_LessonAccess_Lesson FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID),
    CONSTRAINT UC_User_Lesson UNIQUE (UserID, LessonID)  -- tránh trùng lặp
);


CREATE TABLE LessonVocabulary (
    LessonID INT NOT NULL FOREIGN KEY REFERENCES Lessons(LessonID),
    VocabID INT NOT NULL FOREIGN KEY REFERENCES Vocabulary(VocabID),
    PRIMARY KEY (LessonID, VocabID)
);

ALTER TABLE Answers
ADD AnswerNumber INT CHECK (AnswerNumber BETWEEN 1 AND 4);

ALTER TABLE Lessons
ADD Description NVARCHAR(1000);

ALTER TABLE LessonMaterials
ADD CONSTRAINT FK_LessonMaterials_Lessons
FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID);

--Xóa dữ liệu khóa học cũ rồi thêm mới như ở dưới nha

DELETE FROM Answers;
DELETE FROM Questions;
DELETE FROM Quizzes;

DELETE FROM GrammarPoints;
DELETE FROM Kanji;
DELETE FROM Vocabulary;

DELETE FROM LessonMaterials;

-- Sau khi các bảng trên đã xóa, mới xóa Lesson và Course
DELETE FROM Lessons;
DELETE FROM Courses;




-- 1. Tạo khóa học mới N5
INSERT INTO Courses (Title, Description, IsHidden, IsSuggested)
VALUES (N'Khóa học Giao tiếp N5', N'Khóa học tiếng Nhật sơ cấp tập trung vào giao tiếp cơ bản.', 0, 1);

DECLARE @CourseID INT = SCOPE_IDENTITY();
-- 2. Thêm các bài học
INSERT INTO Lessons (CourseID, Title) VALUES
(@CourseID, N'Bài 1: Giới thiệu bản thân'),
(@CourseID, N'Bài 2: Hỏi thăm sức khỏe'),
(@CourseID, N'Bài 3: Hỏi đường đi');


-- Lấy ID từng bài học
DECLARE @Lesson1ID INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 1: Giới thiệu bản thân');
DECLARE @Lesson2ID INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 2: Hỏi thăm sức khỏe');
DECLARE @Lesson3ID INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 3: Hỏi đường đi');

-- Bài 1
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Từ vựng', N'PDF', N'Từ vựng Bài 1', N'files/courseN5/vocabN5/Lesson1.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Kanji', N'PDF', N'Kanji Bài 1', NULL),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 1', N'files/courseN5/grammarN5/Lesson1.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 1: Giới thiệu bản thân'), N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 1', N'files/lesson1_grammar.mp4');

-- Bài 2
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Từ vựng', N'PDF', N'Từ vựng Bài 2', N'files/courseN5/vocabN5/Lesson2.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Kanji', N'PDF', N'Kanji Bài 2', NULL),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 2', N'files/courseN5/grammarN5/Lesson2.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 2: Hỏi thăm sức khỏe'), N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 2', N'files/lesson2_grammar.mp4');

-- Bài 3
INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Từ vựng', N'PDF', N'Từ vựng Bài 3', N'files/courseN5/vocabN5/Lesson3.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Kanji', N'PDF', N'Kanji Bài 3', NULL),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Ngữ pháp', N'PDF', N'Ngữ pháp Bài 3', N'files/courseN5/grammarN5/Lesson3.pdf'),
((SELECT LessonID FROM Lessons WHERE Title = N'Bài 3: Hỏi đường đi'), N'Ngữ pháp', N'Video', N'Video Ngữ pháp Bài 3', N'files/lesson3_grammar.mp4');

