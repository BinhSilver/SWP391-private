drop database Wasabii;
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

-- 3. Payment
CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    PlanID INT NOT NULL,
    Amount DECIMAL(10, 2),
    PaymentDate DATETIME DEFAULT GETDATE(),
    OrderCode NVARCHAR(100),      -- Mã đơn hàng do PayOS sinh ra
    CheckoutUrl NVARCHAR(255),    -- Link thanh toán trả về từ PayOS
    Status NVARCHAR(20),          -- 'PAID', 'PENDING', 'CANCELED'
    CreatedAt DATETIME DEFAULT GETDATE(),

    -- Khóa ngoại
    CONSTRAINT FK_Payments_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_Payments_Plans FOREIGN KEY (PlanID) REFERENCES PremiumPlans(PlanID)
);
GO



--4. Users

CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY,
    RoleID INT FOREIGN KEY REFERENCES Roles(RoleID),
    Email NVARCHAR(255) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255),
    GoogleID NVARCHAR(255),
    FullName NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1,
    IsLocked BIT DEFAULT 0
);

CREATE TABLE UserPremium (
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    PlanID INT FOREIGN KEY REFERENCES PremiumPlans(PlanID),
    StartDate DATETIME,
    EndDate DATETIME,
    PRIMARY KEY (UserID, PlanID)
);

-- 5. Courses & Lessons
CREATE TABLE Courses (
    CourseID INT PRIMARY KEY IDENTITY,
    Title NVARCHAR(255),
    Description NVARCHAR(MAX),
    IsHidden BIT DEFAULT 0
);

CREATE TABLE Lessons (
    LessonID INT PRIMARY KEY IDENTITY,
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Title NVARCHAR(255),
    IsHidden BIT DEFAULT 0
);

-- 6. Enrollment
CREATE TABLE Enrollment (
    EnrollmentID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    EnrolledAt DATETIME DEFAULT GETDATE()
);

-- 7. Course Rating
CREATE TABLE CourseRatings (
    RatingID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    CourseID INT FOREIGN KEY REFERENCES Courses(CourseID),
    Rating INT CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    RatedAt DATETIME DEFAULT GETDATE()
);

-- 8. LessonMaterials
CREATE TABLE LessonMaterials (
    MaterialID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    MaterialType NVARCHAR(20), -- 'PDF', 'Audio', 'Video'
    Title NVARCHAR(255),
    FilePath NVARCHAR(500),
    IsHidden BIT DEFAULT 0
);

-- 9. Vocabulary hệ thống & Tags
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

-- 10. Vocabulary cá nhân
CREATE TABLE UserVocabulary (
    UserVocabID INT PRIMARY KEY IDENTITY,
    UserID INT FOREIGN KEY REFERENCES Users(UserID),
    Word NVARCHAR(100),
    Meaning NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE()
);

-- 11. Kanji
CREATE TABLE Kanji (
    KanjiID INT PRIMARY KEY IDENTITY,
    Character NVARCHAR(10),
    Onyomi NVARCHAR(100),
    Kunyomi NVARCHAR(100),
    Meaning NVARCHAR(255),
    StrokeCount INT
);

-- 12. Grammar Points
CREATE TABLE GrammarPoints (
    GrammarID INT PRIMARY KEY IDENTITY,
    LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID),
    Title NVARCHAR(255),
    Explanation NVARCHAR(MAX)
);

-- 13. Flashcards
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

-- 14. Quiz & Tests
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

-- 15. Questions & Answers
CREATE TABLE Questions (
    QuestionID INT PRIMARY KEY IDENTITY,
    QuizID INT NULL FOREIGN KEY REFERENCES Quizzes(QuizID),
    TestID INT NULL FOREIGN KEY REFERENCES Tests(TestID),
    QuestionText NVARCHAR(MAX),
    CorrectAnswer INT
);

CREATE TABLE Answers (
    AnswerID INT PRIMARY KEY IDENTITY,
    QuestionID INT FOREIGN KEY REFERENCES Questions(QuestionID),
    AnswerText NVARCHAR(MAX),
    AnswerNumber INT
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

-- 17. Feedback & Votes
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
ALTER TABLE Kanji
ADD LessonID INT FOREIGN KEY REFERENCES Lessons(LessonID);
CREATE TABLE VocabularyKanji (
    VocabID INT FOREIGN KEY REFERENCES Vocabulary(VocabID),
    KanjiID INT FOREIGN KEY REFERENCES Kanji(KanjiID),
    PRIMARY KEY (VocabID, KanjiID)
);

ALTER TABLE Questions
ADD TimeLimit INT; -- đơn vị: giây (ví dụ 60 = 1 phút)


-- Thêm cột BirthDate nếu chưa tồn tại
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'BirthDate'
)
BEGIN
    ALTER TABLE Users ADD BirthDate DATE;
END

-- Thêm cột PhoneNumber nếu chưa tồn tại
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'PhoneNumber'
)
BEGIN
    ALTER TABLE Users ADD PhoneNumber NVARCHAR(20);
END

-- Thêm cột JapaneseLevel nếu chưa tồn tại
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'JapaneseLevel'
)
BEGIN
    ALTER TABLE Users ADD JapaneseLevel NVARCHAR(50);
END

-- Thêm cột Address nếu chưa tồn tại
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'Address'
)
BEGIN
    ALTER TABLE Users ADD Address NVARCHAR(255);
END

-- Thêm cột Country nếu chưa tồn tại
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'Country'
)
BEGIN
    ALTER TABLE Users ADD Country NVARCHAR(100);
END

-- Thêm cột Avatar nếu chưa tồn tại
IF NOT EXISTS (
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Users' AND COLUMN_NAME = 'Avatar'
)
BEGIN
    ALTER TABLE Users ADD Avatar NVARCHAR(255);
END


-- Thêm cột giới tính
ALTER TABLE Users
ADD Gender NVARCHAR(10);  -- Ví dụ: 'Nam', 'Nữ'


-- Thiết lập giá trị mặc định cho Gender (nếu cần)
ALTER TABLE Users
ADD CONSTRAINT DF_Users_Gender DEFAULT 'Không xác định' FOR Gender;

-- 20. Rooms & RoomParticipants
CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY IDENTITY(1,1),
    HostUserID INT NOT NULL,
    LanguageLevel NVARCHAR(50) NOT NULL,
    GenderPreference NVARCHAR(20) NOT NULL DEFAULT N'Không yêu cầu',
    MinAge INT CHECK (MinAge >= 0),
    MaxAge INT,  
    CreatedAt DATETIME DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1,

    CONSTRAINT FK_Rooms_Host FOREIGN KEY (HostUserID)
        REFERENCES Users(UserID) ON DELETE CASCADE,

    CONSTRAINT CK_Rooms_GenderPreference CHECK (
        GenderPreference IN (N'Nam', N'Nữ', N'Không yêu cầu')
    )
);CREATE TABLE RoomParticipants (
    RoomID INT NOT NULL,
    UserID INT NOT NULL,
    JoinedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT PK_RoomParticipants PRIMARY KEY (RoomID, UserID),

    CONSTRAINT FK_RoomParticipants_Room FOREIGN KEY (RoomID)
        REFERENCES Rooms(RoomID) ON DELETE CASCADE,

    CONSTRAINT FK_RoomParticipants_User FOREIGN KEY (UserID)
        REFERENCES Users(UserID) ON DELETE NO ACTION
);

INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description)
VALUES 
    (N'Gói Tháng', 25000, 1, N'Sử dụng Premium trong 1 tháng'),
    (N'Gói Năm', 250000, 12, N'Sử dụng Premium trong 12 tháng');



