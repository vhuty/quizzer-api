package com.orsoft.quizzer_api.domain.models.question;

import com.orsoft.quizzer_api.domain.models.answer.Answer;
import com.orsoft.quizzer_api.domain.models.quiz.Quiz;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "questions")
public class Question {
  private UUID id = UUID.randomUUID();
  private String title;
  private QuestionType type;

  private Quiz quiz;
  private Set<Answer> answers = new HashSet<>();

  @Id
  @GeneratedValue
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @Column(nullable = false)
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  @ManyToOne
  @JoinColumn(nullable = false)
  public Quiz getQuiz() {
    return quiz;
  }

  public void setQuiz(Quiz quiz) {
    this.quiz = quiz;
  }

  @OneToMany(
    mappedBy = "question",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  public Set<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(Set<Answer> answers) {
    answers.forEach(answer -> answer.setQuestion(this));

    this.answers = answers;
  }

  @Transient
  public long getPossibleAnswersAmount() {
    Set<Answer> answers = this.getAnswers();

    return this.getType() != QuestionType.MULTIPLE
      ? answers.stream().filter(Answer::getRight).count()
      : answers.size();
  }

  @Transient
  public Set<Answer> getCorrectAnswers() {
    return this.getAnswers().stream().filter(Answer::getRight).collect(Collectors.toSet());
  }

  @Transient
  public Set<Answer> getWrongAnswers() {
    Set<Answer> wrongAnswers = new HashSet<>(this.getAnswers());
    wrongAnswers.removeAll(getCorrectAnswers());
    return wrongAnswers;
  }
}
