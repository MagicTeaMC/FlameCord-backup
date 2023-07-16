package net.md_5.bungee.api.score;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Scoreboard {
  private String name;
  
  private Position position;
  
  private final Map<String, Objective> objectives;
  
  private final Map<String, Score> scores;
  
  private final Map<String, Team> teams;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setPosition(Position position) {
    this.position = position;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Scoreboard))
      return false; 
    Scoreboard other = (Scoreboard)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$position = getPosition(), other$position = other.getPosition();
    if ((this$position == null) ? (other$position != null) : !this$position.equals(other$position))
      return false; 
    Object<Objective> this$objectives = (Object<Objective>)getObjectives(), other$objectives = (Object<Objective>)other.getObjectives();
    if ((this$objectives == null) ? (other$objectives != null) : !this$objectives.equals(other$objectives))
      return false; 
    Object<Score> this$scores = (Object<Score>)getScores(), other$scores = (Object<Score>)other.getScores();
    if ((this$scores == null) ? (other$scores != null) : !this$scores.equals(other$scores))
      return false; 
    Object<Team> this$teams = (Object<Team>)getTeams(), other$teams = (Object<Team>)other.getTeams();
    return !((this$teams == null) ? (other$teams != null) : !this$teams.equals(other$teams));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Scoreboard;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $position = getPosition();
    result = result * 59 + (($position == null) ? 43 : $position.hashCode());
    Object<Objective> $objectives = (Object<Objective>)getObjectives();
    result = result * 59 + (($objectives == null) ? 43 : $objectives.hashCode());
    Object<Score> $scores = (Object<Score>)getScores();
    result = result * 59 + (($scores == null) ? 43 : $scores.hashCode());
    Object<Team> $teams = (Object<Team>)getTeams();
    return result * 59 + (($teams == null) ? 43 : $teams.hashCode());
  }
  
  public String toString() {
    return "Scoreboard(name=" + getName() + ", position=" + getPosition() + ", objectives=" + getObjectives() + ", scores=" + getScores() + ", teams=" + getTeams() + ")";
  }
  
  public Scoreboard() {
    this.objectives = new HashMap<>();
    this.scores = new HashMap<>();
    this.teams = new HashMap<>();
  }
  
  public String getName() {
    return this.name;
  }
  
  public Position getPosition() {
    return this.position;
  }
  
  public Collection<Objective> getObjectives() {
    return Collections.unmodifiableCollection(this.objectives.values());
  }
  
  public Collection<Score> getScores() {
    return Collections.unmodifiableCollection(this.scores.values());
  }
  
  public Collection<Team> getTeams() {
    return Collections.unmodifiableCollection(this.teams.values());
  }
  
  public void addObjective(Objective objective) {
    Preconditions.checkNotNull(objective, "objective");
    Preconditions.checkArgument(!this.objectives.containsKey(objective.getName()), "Objective %s already exists in this scoreboard", objective.getName());
    this.objectives.put(objective.getName(), objective);
  }
  
  public void addScore(Score score) {
    Preconditions.checkNotNull(score, "score");
    this.scores.put(score.getItemName(), score);
  }
  
  public Score getScore(String name) {
    return this.scores.get(name);
  }
  
  public void addTeam(Team team) {
    Preconditions.checkNotNull(team, "team");
    Preconditions.checkArgument(!this.teams.containsKey(team.getName()), "Team %s already exists in this scoreboard", team.getName());
    this.teams.put(team.getName(), team);
  }
  
  public Team getTeam(String name) {
    return this.teams.get(name);
  }
  
  public Objective getObjective(String name) {
    return this.objectives.get(name);
  }
  
  public void removeObjective(String objectiveName) {
    this.objectives.remove(objectiveName);
  }
  
  public void removeScore(String scoreName) {
    this.scores.remove(scoreName);
  }
  
  public void removeTeam(String teamName) {
    this.teams.remove(teamName);
  }
  
  public void clear() {
    this.name = null;
    this.position = null;
    this.objectives.clear();
    this.scores.clear();
    this.teams.clear();
  }
}
