CREATE DATABASE Wasabii;
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
    Avatar VARBINARY(MAX) NULL, --sua de nguoi dung chon file anh de set avartar
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
    MaterialID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    MaterialType NVARCHAR(20),
    FileType NVARCHAR(20),
    Title NVARCHAR(255),
    FilePath NVARCHAR(500),
    IsHidden BIT DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE()
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

INSERT INTO Courses (Title, Description, IsHidden)
VALUES
    (N'Tiếng Nhật Sơ Cấp N5', N'Khóa học dành cho người mới bắt đầu, giúp bạn xây dựng nền tảng tiếng Nhật.', 0),
    (N'Tiếng Nhật Trung Cấp N4', N'Khóa học nâng cao dành cho những ai đã có kiến thức cơ bản.', 0);




INSERT INTO Users (RoleID, Email, PasswordHash, GoogleID, FullName, BirthDate, PhoneNumber, JapaneseLevel, Address, Country, Avatar, Gender) 
VALUES (3, 'teacher@gmail.com', '123456789', NULL, N'Giao vien', '1990-01-01', '1987654321', N'N1', N'Địa chỉ teacher', N'Việt Nam', NULL, N'Nữ'),
		(1, 'nguyenphamthanhbinh02@gmail.com', '123456789', NULL, N'Người Dùng', '2000-01-01', '0123456789', N'N5', N'Địa chỉ user', N'Việt Nam', NULL, N'Nam'),
       (4, 'admin@gmail.com', '123456789', NULL, N'Quản Trị Viên', '1990-01-01', '0987654321', N'N1', N'Địa chỉ admin', N'Việt Nam', NULL, N'Nữ');


-- Đảm bảo các khóa học cũ đều có CreatedAt (nếu migrate data)
UPDATE Courses SET CreatedAt = GETDATE() WHERE CreatedAt IS NULL;

GO
CREATE TRIGGER TR_Users_SetAvatarOnGender
ON Users
AFTER INSERT, UPDATE
AS
BEGIN
    UPDATE Users
    SET Avatar = CASE
                    WHEN i.Gender = N'Nữ' THEN 'img/nu.jpg'
                    ELSE 'img/nam.jpg'
                END
    FROM Users u
    INNER JOIN inserted i ON u.UserID = i.UserID
    WHERE (i.Gender IS NOT NULL AND u.Avatar IS NULL) OR (UPDATE(Gender) AND u.Avatar IS NULL);
END;


-- Tạo khóa học
INSERT INTO Courses (Title, Description, IsHidden, IsSuggested)
VALUES (N'Tiếng Nhật Sơ Cấp N5 - Cơ bản', N'Khóa học dành cho người mới bắt đầu học tiếng Nhật. Bao gồm từ vựng, kanji, ngữ pháp, tài liệu và bài kiểm tra.', 0, 1);

DECLARE @CourseID INT = SCOPE_IDENTITY();

INSERT INTO Lessons (CourseID, Title)
VALUES 
(@CourseID, N'Bài 1: Giới thiệu bản thân'),
(@CourseID, N'Bài 2: Gia đình'),
(@CourseID, N'Bài 3: Ngày tháng năm');
DECLARE @Lesson1 INT = (SELECT LessonID FROM Lessons WHERE CourseID = @CourseID AND Title = N'Bài 1: Giới thiệu bản thân');

INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath)
VALUES 
(@Lesson1, N'Video', N'mp4', N'Giới thiệu bản thân (video)', 'media/lesson1/intro.mp4'),
(@Lesson1, N'PDF', N'pdf', N'Tài liệu ngữ pháp', 'media/lesson1/grammar.pdf');

INSERT INTO Vocabulary (Word, Meaning, Reading, Example)
VALUES 
(N'わたし', N'Tôi', N'watashi', N'わたしはリンです。'),
(N'がくせい', N'Học sinh', N'gakusei', N'わたしはがくせいです。');


INSERT INTO Kanji (Character, Onyomi, Kunyomi, Meaning, StrokeCount, LessonID)
VALUES 
(N'人', N'ジン, ニン', N'ひと', N'Người', 2, @Lesson1),
(N'名', N'メイ, ミョウ', N'な', N'Tên', 6, @Lesson1);



INSERT INTO GrammarPoints (LessonID, Title, Explanation)
VALUES 
(@Lesson1, N'は (wa) chủ đề câu', N'Dùng để chỉ chủ đề chính của câu. Ví dụ: わたしはリンです。');


-- Quiz
INSERT INTO Quizzes (LessonID, Title)
VALUES (@Lesson1, N'Quiz - Giới thiệu bản thân');

DECLARE @QuizID INT = SCOPE_IDENTITY();

-- Câu hỏi
INSERT INTO Questions (QuizID, QuestionText)
VALUES (@QuizID, N'Từ "わたし" nghĩa là gì?');

DECLARE @QuestionID INT = SCOPE_IDENTITY();

-- Đáp án
INSERT INTO Answers (QuestionID, AnswerText, IsCorrect)
VALUES 
(@QuestionID, N'Tôi', 1),
(@QuestionID, N'Bạn', 0),
(@QuestionID, N'Anh ấy', 0);

